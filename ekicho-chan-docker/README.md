# えきちょーちゃん：ローカルDocker環境

Android・Spring Bootと同じ親フォルダに置く開発用DB設定です。
元の C:/Users/jink0/docker/postgresql は変更しません。

## ファイルと初期化の流れ

- compose.yml：PostgreSQL 17、Mailpit、Spring Bootをまとめて管理。
- .env.example：管理者postgresのパスワード設定例。.envにコピーして編集する。
- init/01_ekicho_chan_init_db.sql：DB、アプリ用ユーザーekicho1、テーブル、権限。
- init/02_station_master_insert.sql：駅データ。
- init/03_image_insert.sql：画像データ。

空のボリュームで初回起動したときだけ、SQLがファイル名順に実行されます。
POSTGRES_DBは指定しません。SQL内でekicho_chan_localを作るためです。
保存先はDocker管理のekicho-chan-dev_pg17-dataで、このWindowsフォルダ内ではありません。
PostgreSQL 15の既存データボリュームは流用しません。

## 初回起動（PowerShell）

Docker Desktopを起動してから実行します。

```powershell
cd C:\Users\jink0\Android-project\ekicho-chan-app\ekicho-chan-docker
Copy-Item .env.example .env
notepad .env
```

.envのREPLACE_WITH_LOCAL_ADMIN_PASSWORDとREPLACE_WITH_LOCAL_APPLICATION_PASSWORDを変更して保存。
既存.envがある場合はコピーで上書きしないでください。パスワードは共有しないでください。
POSTGRES_PASSWORDは管理者postgres用、SPRING_DB_PASSWORDはSQLで作るekicho1用です。
コピーしたSQLには従来の開発用パスワードが含まれます。本番には使わないでください。

```powershell
docker compose config --quiet
docker compose up -d
docker compose ps
docker compose logs --tail 100 postgres
docker compose logs --tail 100 spring
```

初回はイメージ取得・画像データ投入に時間がかかる場合があります。
healthyはサーバーの応答確認であり、SQL初期化の完全成功を保証しません。
ERRORがないこととデータ件数を確認してください。

```powershell
docker compose exec postgres psql -U postgres -d ekicho_chan_local -c "SELECT current_database();"
docker compose exec postgres psql -U postgres -d ekicho_chan_local -c "SELECT count(*) AS station_count FROM station_master; SELECT count(*) AS image_count FROM image;"
```

上記はコンテナ内部の確認です。STSからのパスワード認証は別途確認します。

## Spring Boot（Docker）

- API：`http://127.0.0.1:18080`
- ヘルスチェック：`http://127.0.0.1:18080/actuator/health`
- Mailpit：`http://127.0.0.1:8025`
- `SPRING_PORT`を変更するとホスト側のAPIポートを変更できます。
- 実機Androidから接続する場合は、`.env`の`SPRING_BIND_ADDRESS`を`0.0.0.0`に変更し、PCのIPv4アドレスと`SPRING_PORT`をAndroid側へ設定します。
- ローカルHTTP用にCookieのSecure属性だけをComposeで無効化しています。本番環境では有効のままです。

## Spring Boot（STSで起動する場合）

- SPRING_PROFILES_ACTIVE=local
- URL：jdbc:postgresql://localhost:5432/ekicho_chan_local
- ユーザー：ekicho1
- パスワード：application-local.propertiesと初期化SQLの設定を一致させる。

## 日常の起動・停止

Docker DesktopのContainersにekicho-chan-devが表示されます。
このフォルダのPowerShellでも操作できます。

```powershell
docker compose stop
docker compose start
```

ソースを変更した後にDocker版Spring Bootを作り直す場合：

```powershell
docker compose up -d --build spring
docker compose ps
```

## 注意

- ポート競合の場合は既存DBを消さず、5432の使用状況を確認。
- STS版とDocker版Spring Bootを同時に使う場合、既定ではSTSが8080、Dockerが18080を使用します。
- SQLにはDROP TABLEが含まれます。既存DBへ手動で再実行しないこと。
- 初期化に失敗した場合、再起動だけではSQLは再実行されません。ログを確認し、相談すること。
- .envを変更しても既存DBのパスワードは変わりません。
- ボリューム削除やdocker compose down -vはDBデータを失うため実行しないこと。
- 今回はファイル準備のみ。SQLのPostgreSQL 17上での実行結果は起動後に確認します。

公式仕様：https://hub.docker.com/_/postgres

## ポートフォリオ公開モード

本番EC2の`.env.production`へ次を設定してからコンテナを再作成します。

```dotenv
APP_PORTFOLIO_MODE='true'
```

このモードでは、ユーザー登録・ログイン・アカウント・管理者APIを拒否し、
`/api/v1/portfolio/stations/**`だけをログインなしで利用できます。
獲得履歴はPostgreSQLへ保存せず、Reactが利用者のブラウザのlocalStorageへ保存します。

通常の認証機能へ戻す場合は`false`へ変更します。設定変更後は`docker compose restart`ではなく、
次のようにSpringコンテナを再作成してください。

```powershell
docker compose --env-file .env.production -f compose.production.yml up -d --force-recreate spring nginx
```

## Nginxのレート制限

公開APIにはNginxの`limit_req`を設定しています。超過したリクエストには
`429 Too Many Requests`を返します。

| 対象 | 平均レート | 瞬間許容量 |
| --- | ---: | ---: |
| API全般（IP単位） | 10件/秒 | 30件 |
| API全体 | 100件/秒 | 200件 |
| 登録・ログインなどの認証API | 10件/分 | 5件 |
| 公開デモのガチャ・位置取得 | 30件/分 | 8件 |

CloudFrontは実クライアントIPを`X-Forwarded-For`の末尾へ追加するため、Nginxは
その末尾のアドレスをIP単位の制限キーとして使用します。EC2のセキュリティグループと
`X-Origin-Verify`によるCloudFront以外からのアクセス制限は、引き続き必要です。

設定変更後は、デプロイ前に構文を確認します。

```powershell
docker run --rm --add-host spring:127.0.0.1 `
  -e ORIGIN_VERIFY_SECRET=test-only `
  -v "${PWD}/nginx/templates:/etc/nginx/templates:ro" `
  nginx:1.28-alpine nginx -t
```
