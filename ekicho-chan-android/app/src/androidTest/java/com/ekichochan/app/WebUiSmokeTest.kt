package com.ekichochan.app

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import org.junit.Rule
import org.junit.Test
import org.junit.Ignore

class WebUiSmokeTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private fun capture(name: String) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val directory = File(context.getExternalFilesDir(null), "ui-review")
        directory.mkdirs()
        val bitmap = compose.onAllNodes(isRoot()).onLast().captureToImage().asAndroidBitmap()
        File(directory, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    @Test fun unauthenticatedLaunchShowsLoginOnly() {
        compose.waitUntil(5000) { compose.onAllNodesWithText("新規登録フォームへ").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("新規登録フォームへ").assertIsDisplayed()
        compose.onNodeWithText("位置情報").assertDoesNotExist()
        capture("login")
    }

    @Ignore("Authenticated screen fixture will be added with protected API integration")
    @Test fun gachaOpensAndClosesSampleResult() {
        compose.onNodeWithText("ガチャ").performClick()
        compose.onNodeWithText("GETする").performClick()
        compose.onNodeWithText("GET結果の表示例").assertIsDisplayed()
        capture("gacha-result")
        compose.onNodeWithText("閉じる").performScrollTo().performClick()
        compose.onNodeWithText("GETする").assertIsDisplayed()
    }

    @Ignore("Authenticated screen fixture will be added with protected API integration")
    @Test fun collectionSearchAndDetailWork() {
        compose.onNodeWithText("閲覧").performClick()
        compose.waitUntil(5000) { compose.onAllNodesWithText("はじまり駅（学習用）").fetchSemanticsNodes().isNotEmpty() }
        capture("collection")
        compose.onNodeWithText("駅名・地域（学習用データ内）").performTextInput("海辺")
        compose.onNodeWithText("検索", substring = false).performClick()
        compose.waitUntil(5000) { compose.onAllNodesWithText("表示件数：1件（学習用）").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("海辺駅（学習用）").performScrollTo().performClick()
        compose.onNodeWithText("駅の詳細").assertIsDisplayed()
        capture("detail")
        compose.onNodeWithText("コレクションへ戻る").performClick()
        compose.onNodeWithText("リセット").performClick()
        compose.onNodeWithText("検索を押すと学習用の一覧を表示します。").assertExists()
    }

    @Ignore("Authenticated screen fixture will be added with protected API integration")
    @Test fun locationRequiresSelectionBeforeDemoResult() {
        compose.onNodeWithText("位置情報").performClick()
        capture("location")
        compose.onNodeWithText("GETする").performScrollTo().performClick()
        compose.onNodeWithText("都道府県と駅を選択してください。").assertIsDisplayed()
        compose.onNodeWithText("閉じる").performClick()
        // No live-server dependency in this smoke test. Loading/retry is unit-tested.
        compose.onNodeWithText("駅一覧・通常画像・駅の説明をサーバーから取得します。GPS・距離判定はデモで、獲得・保存は行いません。").assertExists()
    }

    @Test fun loginValidatesWithoutAuthentication() {
        compose.waitUntil(5000) { compose.onAllNodesWithText("新規登録フォームへ").fetchSemanticsNodes().isNotEmpty() }
        compose.onNode(hasText("ログイン") and hasClickAction()).performScrollTo().performClick()
        compose.onNodeWithText("メールアドレスの形式を確認してください。").assertExists()
        compose.onNodeWithText("新規登録フォームへ").performScrollTo().performClick()
        compose.onNodeWithText("表示名").assertExists()
        compose.onNodeWithText("パスワードの確認").assertExists()
    }
}
