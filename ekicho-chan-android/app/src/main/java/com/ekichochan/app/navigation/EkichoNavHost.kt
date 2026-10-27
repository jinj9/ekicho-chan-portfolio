package com.ekichochan.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.padding
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ekichochan.app.ui.location.LocationScreen
import com.ekichochan.app.ui.login.LoginScreen
import com.ekichochan.app.ui.login.AccountScreen
import com.ekichochan.app.ui.login.AccountViewModel
import com.ekichochan.app.ui.login.PasswordResetScreen
import com.ekichochan.app.ui.login.PasswordResetViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.ekichochan.app.ui.collection.CollectionScreen
import com.ekichochan.app.ui.collection.LiveCollectionViewModel
import com.ekichochan.app.ui.collection.LiveStationDetailScreen
import com.ekichochan.app.ui.collection.LiveStationDetailViewModel
import com.ekichochan.app.ui.gacha.LiveGachaScreen
import com.ekichochan.app.ui.gacha.LiveGachaViewModel
import com.ekichochan.app.ui.home.HomeScreen
import androidx.compose.ui.platform.LocalContext
import com.ekichochan.app.EkichoChanApplication
import com.ekichochan.app.ui.login.RegistrationViewModel
import com.ekichochan.app.data.repository.AuthSessionState

private const val HOME_ROUTE = "home"
private const val GACHA_ROUTE = "gacha"
private const val COLLECTION_ROUTE = "collection"
private const val STATION_ID_ARGUMENT = "stationId"
private const val STATION_DETAIL_ROUTE = "stationDetail"
private const val LOGIN_ROUTE = "login"
private const val ACCOUNT_ROUTE = "account"
private const val PASSWORD_RESET_ROUTE = "passwordReset"

@Composable
fun EkichoNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val application = LocalContext.current.applicationContext as EkichoChanApplication
    val authState by application.authSessionManager.state.collectAsStateWithLifecycle()
    val authenticatedUser = (authState as? AuthSessionState.SignedIn)?.user
    if (authState == AuthSessionState.Checking || authState == AuthSessionState.SigningOut) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    LaunchedEffect(authState, currentRoute) {
        when {
            authenticatedUser == null && currentRoute != null && currentRoute !in setOf(LOGIN_ROUTE, PASSWORD_RESET_ROUTE) ->
                navController.navigate(LOGIN_ROUTE) { popUpTo(0) }
            authenticatedUser != null && currentRoute in setOf(LOGIN_ROUTE, PASSWORD_RESET_ROUTE) ->
                navController.navigate(HOME_ROUTE) { popUpTo(currentRoute!!) { inclusive = true }; launchSingleTop = true }
        }
    }
    val navigateTopLevel: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(HOME_ROUTE) { inclusive = false }
            launchSingleTop = true
        }
    }

    WebAppFrame(route = currentRoute, user = authenticatedUser, onNavigate = navigateTopLevel,
        onLogout = {
            application.authSessionManager.logout()
        }) { padding ->
    NavHost(
        navController = navController,
        startDestination = if (authenticatedUser == null) LOGIN_ROUTE else HOME_ROUTE,
        modifier = modifier.padding(padding)
    ) {
        composable(route = HOME_ROUTE) {
            HomeScreen(
                displayName = authenticatedUser?.displayName.orEmpty(),
                onNavigateToGacha = {
                    navController.navigate(GACHA_ROUTE) {
                        // 連続タップで同じ画面が履歴に重複するのを防ぐ。
                        launchSingleTop = true
                    }
                },
                onNavigateToCollection = {
                    navController.navigate(COLLECTION_ROUTE) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(route = "location") {
            val locationViewModel: com.ekichochan.app.ui.location.LocationViewModel = viewModel {
                com.ekichochan.app.ui.location.LocationViewModel(
                    com.ekichochan.app.data.repository.RemoteLocationStationRepository(),
                    application.accountStationRepository
                )
            }
            LocationScreen(
                state = locationViewModel.state.collectAsStateWithLifecycle().value,
                onPrefectureSelected = locationViewModel::selectPrefecture,
                onRetry = locationViewModel::retry,
                onLocating = locationViewModel::locating,
                onLocationReady = locationViewModel::obtain,
                onLocationUnavailable = locationViewModel::locationUnavailable,
                onResultDismissed = locationViewModel::consumeResult
            )
        }
        composable(route = LOGIN_ROUTE) {
            val registrationViewModel: RegistrationViewModel = viewModel { RegistrationViewModel(application.authRepository) }
            LoginScreen(registrationViewModel, application.authSessionManager,
                onLoginSuccess = {
                    navController.navigate(HOME_ROUTE) {
                        popUpTo(LOGIN_ROUTE) { inclusive = true }
                        launchSingleTop = true
                    }
                }, onForgotPassword = { navController.navigate(PASSWORD_RESET_ROUTE) })
        }
        composable(route = PASSWORD_RESET_ROUTE) {
            val resetViewModel: PasswordResetViewModel = viewModel { PasswordResetViewModel(application.authRepository) }
            PasswordResetScreen(resetViewModel, onBackToLogin = { navController.popBackStack() })
        }
        composable(route = ACCOUNT_ROUTE) {
            authenticatedUser?.let { user ->
                val accountViewModel: AccountViewModel = viewModel {
                    AccountViewModel(application.authRepository, application.authSessionManager)
                }
                AccountScreen(user, accountViewModel)
            }
        }
        composable(route = COLLECTION_ROUTE) {
            val collectionViewModel: LiveCollectionViewModel = viewModel {
                LiveCollectionViewModel(application.accountStationRepository)
            }
            val collectionState = collectionViewModel.state.collectAsStateWithLifecycle().value
            CollectionScreen(
                uiState = collectionState,
                onNextSampleState = collectionViewModel::reload,
                onRestoreSampleStations = collectionViewModel::reload,
                liveMode = true,
                onBack = { navController.popBackStack() },
                onStationClick = { stationId ->
                    navController.navigate("$STATION_DETAIL_ROUTE/$stationId") {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(
            route = "$STATION_DETAIL_ROUTE/{$STATION_ID_ARGUMENT}",
            arguments = listOf(navArgument(STATION_ID_ARGUMENT) { type = NavType.IntType })
        ) { backStackEntry ->
            val stationId = backStackEntry.arguments?.getInt(STATION_ID_ARGUMENT)
            // 詳細画面の履歴ごとに保持する。再描画だけでは取得し直さない。
            val detailViewModel: LiveStationDetailViewModel = viewModel {
                LiveStationDetailViewModel(
                    stationId = stationId,
                    repository = application.accountStationRepository
                )
            }
            LiveStationDetailScreen(
                state = detailViewModel.state.collectAsStateWithLifecycle().value,
                onBack = { navController.popBackStack() }
            )
        }
        composable(route = GACHA_ROUTE) {
            // このガチャ画面の履歴エントリに紐づくViewModelを取得する。
            val gachaViewModel: LiveGachaViewModel = viewModel {
                LiveGachaViewModel(application.accountStationRepository)
            }
            LiveGachaScreen(
                state = gachaViewModel.state.collectAsStateWithLifecycle().value,
                onDraw = gachaViewModel::draw,
                onBack = {
                    // ホームを新しく積まず、ガチャを履歴から取り除いて戻る。
                    navController.popBackStack()
                }
            )
        }
    }
    }
}
