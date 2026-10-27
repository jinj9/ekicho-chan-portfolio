package com.ekichochan.app.ui.components

import com.ekichochan.app.R

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ekichochan.app.ui.theme.*

@Composable
fun SkyBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    StationSceneBackground(modifier = modifier, content = content)
}

@Composable
fun DemoNotice(modifier: Modifier = Modifier) {
    Text(
        stringResource(R.string.web_demo_notice), modifier,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun GoldGetButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = stringResource(R.string.web_get),
    enabled: Boolean = true
) {
    Button(
        onClick, modifier.heightIn(min = 72.dp), enabled = enabled,
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(3.dp, WebRed),
        colors = ButtonDefaults.buttonColors(containerColor = WebGold, contentColor = WebNavy),
        contentPadding = PaddingValues(horizontal = 28.dp, vertical = 18.dp)
    ) { Text(label, fontSize = 26.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center) }
}

@Composable
fun SampleCharacter(modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Image(
            painterResource(R.drawable.sample_character),
            contentDescription = stringResource(R.string.web_sample_image),
            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
            contentScale = ContentScale.Fit
        )
        Text(stringResource(R.string.web_sample_image), style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun SampleResultDialog(stationName: String, description: String, onDismiss: () -> Unit, onAgain: (() -> Unit)? = null, notice: String = stringResource(R.string.web_demo_notice)) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(12.dp)) {
            Column(
                Modifier.heightIn(max = 620.dp).verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(stringResource(R.string.web_result_title), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                Text(notice, style = MaterialTheme.typography.bodySmall)
                SampleCharacter(Modifier.fillMaxWidth())
                Text(stationName, style = MaterialTheme.typography.titleLarge)
                Text(description)
                if (onAgain != null) {
                    OutlinedButton(onClick = onAgain, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.web_again))
                    }
                }
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.web_close)) }
            }
        }
    }
}
