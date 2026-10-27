package com.ekichochan.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ekichochan.app.navigation.EkichoNavHost
import com.ekichochan.app.ui.theme.EkichoChanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ヘッダーの紺色背景上でもステータスバーの時刻・アイコンが見えるよう、
        // 常に明るい前景色を使用する。
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        setContent {
            EkichoChanTheme {
                EkichoNavHost()
            }
        }
    }
}
