# Run from Windows PowerShell 5.1 or PowerShell 7.
# Example:
# powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\deploy-production.ps1 -ImageTag 20260926-02

[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [ValidatePattern('^[0-9A-Za-z][0-9A-Za-z._-]{0,127}$')]
    [string]$ImageTag,

    [string]$Profile = 'ekichochan-admin',
    [string]$Region = 'ap-northeast-1',
    [Parameter(Mandatory = $true)]
    [ValidatePattern('^\d{12}$')]
    [string]$ExpectedAccountId,
    [string]$Repository = 'ekicho-chan-spring',
    [string]$InstanceName = 'ekicho-chan-prod',
    [string]$RemoteDirectory = '/opt/ekicho-chan',
    [string]$PublicHealthUrl = 'https://ekichochan.com/api/v1/web/auth/csrf',
    [switch]$SkipConfirmation
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

function Assert-LastCommand {
    param([Parameter(Mandatory = $true)][string]$Step)
    if ($LASTEXITCODE -ne 0) {
        throw "$Step failed. Exit code: $LASTEXITCODE"
    }
}

function Invoke-AwsIdentityCheck {
    $accountId = aws sts get-caller-identity `
        --profile $Profile `
        --query Account `
        --output text 2>$null

    if ($LASTEXITCODE -ne 0) {
        Write-Host 'The AWS SSO session is unavailable. Starting browser authentication.'
        aws sso login --profile $Profile
        Assert-LastCommand 'AWS SSO login'

        $accountId = aws sts get-caller-identity `
            --profile $Profile `
            --query Account `
            --output text
        Assert-LastCommand 'AWS identity check'
    }

    $accountId = "$accountId".Trim()
    if ($accountId -ne $ExpectedAccountId) {
        throw "Unexpected AWS account: $accountId (expected: $ExpectedAccountId)"
    }
    Write-Host "AWS account verified: $accountId"
}

$projectDirectory = $PSScriptRoot
$registry = "$ExpectedAccountId.dkr.ecr.$Region.amazonaws.com"
$localImage = "${Repository}:${ImageTag}"
$remoteImage = "${registry}/${Repository}:${ImageTag}"
$parametersFile = $null

Set-Location $projectDirectory

Write-Host "Target image: $remoteImage"
if (-not $SkipConfirmation) {
    $answer = Read-Host 'Type DEPLOY to continue'
    if ($answer -cne 'DEPLOY') {
        Write-Host 'Deployment cancelled.'
        exit 0
    }
}

Write-Host '1. Checking required commands.'
foreach ($command in @('aws', 'docker')) {
    if (-not (Get-Command $command -ErrorAction SilentlyContinue)) {
        throw "Required command was not found: $command"
    }
}
if (-not (Test-Path -LiteralPath '.\gradlew.bat')) {
    throw 'gradlew.bat was not found.'
}

Write-Host '2. Checking AWS authentication.'
Invoke-AwsIdentityCheck

Write-Host '3. Running Spring Boot tests and creating bootJar.'
& .\gradlew.bat test bootJar
Assert-LastCommand 'Gradle test and bootJar'

Write-Host '4. Building the linux/amd64 Docker image.'
docker buildx build `
    --platform linux/amd64 `
    --provenance=false `
    --sbom=false `
    --load `
    --tag $localImage `
    .
Assert-LastCommand 'Docker image build'

Write-Host '5. Logging in to Amazon ECR.'
$ecrPassword = aws ecr get-login-password `
    --region $Region `
    --profile $Profile
Assert-LastCommand 'ECR password retrieval'
try {
    $ecrPassword | docker login `
        --username AWS `
        --password-stdin $registry
    Assert-LastCommand 'Docker ECR login'
}
finally {
    $ecrPassword = $null
}

Write-Host '6. Tagging and pushing the image.'
docker tag $localImage $remoteImage
Assert-LastCommand 'Docker image tag'
docker push $remoteImage
Assert-LastCommand 'Docker image push'

aws ecr describe-images `
    --repository-name $Repository `
    --image-ids "imageTag=$ImageTag" `
    --region $Region `
    --profile $Profile `
    --query 'imageDetails[0].imageDigest' `
    --output text | Out-Null
Assert-LastCommand 'ECR image verification'

Write-Host '7. Resolving the running EC2 instance.'
$instanceText = aws ec2 describe-instances `
    --region $Region `
    --profile $Profile `
    --filters "Name=tag:Name,Values=$InstanceName" 'Name=instance-state-name,Values=running' `
    --query 'Reservations[].Instances[].InstanceId' `
    --output text
Assert-LastCommand 'EC2 instance lookup'

$instanceIds = @("$instanceText" -split '\s+' | Where-Object { $_ })
if ($instanceIds.Count -ne 1) {
    throw "Expected one running EC2 instance named '$InstanceName', found $($instanceIds.Count)."
}
$instanceId = $instanceIds[0]
Write-Host "EC2 instance: $instanceId"

$managedInstanceId = aws ssm describe-instance-information `
    --region $Region `
    --profile $Profile `
    --filters "Key=InstanceIds,Values=$instanceId" `
    --query 'InstanceInformationList[0].InstanceId' `
    --output text
Assert-LastCommand 'SSM managed instance check'
if ("$managedInstanceId".Trim() -ne $instanceId) {
    throw "EC2 instance $instanceId is not ready in Systems Manager."
}

Write-Host '8. Sending the deployment command through Systems Manager.'
$remoteCommands = @(
    'set -Eeuo pipefail',
    "cd '$RemoteDirectory'",
    "REGION='$Region'",
    "REGISTRY='$registry'",
    "NEW_IMAGE='$remoteImage'",
    'test -f .env.production',
    'test -f compose.production.yml',
    'OLD_IMAGE="$(grep ''^SPRING_IMAGE='' .env.production | cut -d= -f2-)"',
    'test -n "$OLD_IMAGE"',
    'echo "Previous image: $OLD_IMAGE"',
    'echo "New image: $NEW_IMAGE"',
    'aws ecr get-login-password --region "$REGION" | docker login --username AWS --password-stdin "$REGISTRY"',
    'docker pull "$NEW_IMAGE"',
    'rollback() {',
    '  trap - ERR',
    '  echo "Deployment failed. Restoring: $OLD_IMAGE" >&2',
    '  sed -i "s|^SPRING_IMAGE=.*$|SPRING_IMAGE=$OLD_IMAGE|" .env.production',
    '  docker compose --env-file .env.production -f compose.production.yml up -d spring || true',
    '}',
    'trap rollback ERR',
    'sed -i "s|^SPRING_IMAGE=.*$|SPRING_IMAGE=$NEW_IMAGE|" .env.production',
    'docker compose --env-file .env.production -f compose.production.yml up -d spring',
    'CONTAINER_ID="$(docker compose --env-file .env.production -f compose.production.yml ps -q spring)"',
    'test -n "$CONTAINER_ID"',
    'for attempt in $(seq 1 36); do',
    '  HEALTH="$(docker inspect --format ''{{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Status}}{{end}}'' "$CONTAINER_ID")"',
    '  echo "Health check $attempt/36: $HEALTH"',
    '  if [ "$HEALTH" = "healthy" ]; then break; fi',
    '  if [ "$HEALTH" = "unhealthy" ]; then docker logs --tail 100 "$CONTAINER_ID"; false; fi',
    '  sleep 5',
    'done',
    'test "$HEALTH" = "healthy"',
    'trap - ERR',
    'docker compose --env-file .env.production -f compose.production.yml ps',
    'echo "Deployment completed successfully."'
)

try {
    $parametersFile = [System.IO.Path]::GetTempFileName()
    $parametersJson = @{ commands = $remoteCommands } | ConvertTo-Json -Depth 4
    $utf8WithoutBom = New-Object System.Text.UTF8Encoding($false)
    [System.IO.File]::WriteAllText($parametersFile, $parametersJson, $utf8WithoutBom)

    $commandId = aws ssm send-command `
        --instance-ids $instanceId `
        --document-name 'AWS-RunShellScript' `
        --comment "Deploy $Repository $ImageTag" `
        --parameters "file://$parametersFile" `
        --timeout-seconds 600 `
        --region $Region `
        --profile $Profile `
        --query 'Command.CommandId' `
        --output text
    Assert-LastCommand 'SSM deployment command submission'
    $commandId = "$commandId".Trim()
    if ([string]::IsNullOrWhiteSpace($commandId) -or $commandId -eq 'None') {
        throw 'The SSM command ID was not returned.'
    }
    Write-Host "SSM command: $commandId"

    aws ssm wait command-executed `
        --command-id $commandId `
        --instance-id $instanceId `
        --region $Region `
        --profile $Profile

    $invocationJson = aws ssm get-command-invocation `
        --command-id $commandId `
        --instance-id $instanceId `
        --region $Region `
        --profile $Profile `
        --output json
    Assert-LastCommand 'SSM deployment result retrieval'
    $invocation = $invocationJson | ConvertFrom-Json

    if (-not [string]::IsNullOrWhiteSpace($invocation.StandardOutputContent)) {
        Write-Host $invocation.StandardOutputContent
    }
    if (-not [string]::IsNullOrWhiteSpace($invocation.StandardErrorContent)) {
        Write-Warning $invocation.StandardErrorContent
    }
    if ($invocation.Status -ne 'Success') {
        throw "EC2 deployment failed. SSM status: $($invocation.Status)"
    }
}
finally {
    if ($parametersFile -and (Test-Path -LiteralPath $parametersFile)) {
        Remove-Item -LiteralPath $parametersFile -Force
    }
}

Write-Host '9. Checking the public API.'
try {
    $response = Invoke-WebRequest -Uri $PublicHealthUrl -Method Get -TimeoutSec 30
    if ($response.StatusCode -ne 204) {
        Write-Warning "Public API returned HTTP $($response.StatusCode)."
    }
    else {
        Write-Host 'Public API returned HTTP 204.'
    }
}
catch {
    Write-Warning "The EC2 deployment succeeded, but the public API check failed: $($_.Exception.Message)"
}

Write-Host ''
Write-Host "Production deployment completed: $remoteImage" -ForegroundColor Green
