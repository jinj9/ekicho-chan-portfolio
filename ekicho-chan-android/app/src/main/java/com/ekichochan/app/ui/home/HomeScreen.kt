package com.ekichochan.app.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ekichochan.app.R
import com.ekichochan.app.ui.theme.EkichoChanTheme

@Composable
fun HomeScreen(
    displayName: String,
    onNavigateToGacha: () -> Unit,
    onNavigateToCollection: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HomeIntroduction(displayName)
        Card(modifier = Modifier.fillMaxWidth(), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(stringResource(R.string.web_memo_title), style = MaterialTheme.typography.titleMedium)
                Text(stringResource(R.string.web_memo_body))
            }
        }
        Text(
            text = stringResource(R.string.home_menu_title),
            style = MaterialTheme.typography.titleLarge
        )
        GachaMenuCard(onNavigateToGacha = onNavigateToGacha)
        CollectionMenuCard(onNavigateToCollection = onNavigateToCollection)
        Text(
            text = stringResource(R.string.home_sample_notice),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun HomeIntroduction(displayName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                stringResource(R.string.web_welcome, displayName), Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold, textAlign = TextAlign.Center
            )
            HomeDescription()
        }
    }
}

@Composable
private fun GachaMenuCard(onNavigateToGacha: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.home_gacha_title),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(R.string.home_gacha_description),
                style = MaterialTheme.typography.bodyMedium
            )
            Button(onClick = onNavigateToGacha, modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(R.string.open_gacha))
            }
        }
    }
}

@Composable
private fun CollectionMenuCard(onNavigateToCollection: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.home_collection_title),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(R.string.home_collection_description),
                style = MaterialTheme.typography.bodyMedium
            )
            OutlinedButton(
                onClick = onNavigateToCollection,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.open_collection))
            }
        }
    }
}

@Composable
private fun HomeTitle(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.app_name),
        modifier = modifier,
        style = MaterialTheme.typography.headlineMedium
    )
}

@Composable
private fun HomeDescription(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.home_description),
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge
    )
}

@Preview(name = "Light", showBackground = true, widthDp = 360, heightDp = 800)
@Preview(
    name = "Dark",
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    name = "Large text / compact",
    showBackground = true,
    widthDp = 320,
    heightDp = 640,
    fontScale = 1.5f
)
@Composable
private fun HomeScreenPreview() {
    EkichoChanTheme(dynamicColor = false) {
        Surface {
            HomeScreen(displayName = "テストユーザー", onNavigateToGacha = {}, onNavigateToCollection = {})
        }
    }
}
