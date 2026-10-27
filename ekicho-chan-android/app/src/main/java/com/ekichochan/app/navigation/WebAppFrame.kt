package com.ekichochan.app.navigation

import com.ekichochan.app.R

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ekichochan.app.ui.components.SkyBackground
import com.ekichochan.app.ui.theme.WebGold
import com.ekichochan.app.ui.theme.WebNavy
import com.ekichochan.app.ui.theme.WebRed
import kotlinx.coroutines.launch
import com.ekichochan.app.data.model.AuthUser

private data class WebDestination(val route: String, val label: Int, val icon: Int)
private val destinations = listOf(
    WebDestination("home", R.string.web_home, R.drawable.ic_web_home),
    WebDestination("gacha", R.string.web_gacha, R.drawable.ic_web_train),
    WebDestination("location", R.string.web_location, R.drawable.ic_web_location),
    WebDestination("collection", R.string.web_collection, R.drawable.ic_web_collection)
)

@Composable
fun WebAppFrame(route: String?, user: AuthUser?, onNavigate: (String) -> Unit,
    onLogout: () -> Unit, content: @Composable (PaddingValues) -> Unit) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val selected = if (route?.startsWith("stationDetail") == true) "collection" else route
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
                    Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge)
                    if (user == null) {
                        Text(stringResource(R.string.web_guest), Modifier.padding(vertical = 16.dp))
                    } else {
                        Card(onClick = {
                            scope.launch { drawerState.close() }
                            onNavigate("account")
                        }, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            border = BorderStroke(2.dp, WebGold)) {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(user.displayName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(user.email, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                    if (user != null) destinations.forEach { destination ->
                        NavigationDrawerItem(
                            label = { Text(stringResource(destination.label)) },
                            selected = selected == destination.route,
                            onClick = { scope.launch { drawerState.close() }; onNavigate(destination.route) }
                        )
                    }
                    if (user == null) NavigationDrawerItem(
                        label = { Text(stringResource(R.string.web_login)) }, selected = route == "login",
                        onClick = { scope.launch { drawerState.close() }; onNavigate("login") })
                    else NavigationDrawerItem(
                        label = { Text(stringResource(R.string.auth_logout)) }, selected = false,
                        onClick = { scope.launch { drawerState.close() }; onLogout() })
                    TextButton(onClick = { scope.launch { drawerState.close() } }) { Text(stringResource(R.string.web_close)) }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                Surface(color = WebNavy, contentColor = WebGold) {
                    Column(Modifier.statusBarsPadding()) {
                        Row(Modifier.fillMaxWidth().heightIn(min = 56.dp).padding(start = 16.dp)) {
                            Text(
                                stringResource(R.string.app_name),
                                Modifier.weight(1f).padding(vertical = 14.dp),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    painterResource(R.drawable.ic_menu),
                                    contentDescription = stringResource(R.string.web_menu),
                                    tint = WebGold
                                )
                            }
                        }
                        HorizontalDivider(thickness = 3.dp, color = WebRed)
                    }
                }
            },
            bottomBar = {
                if (user != null) NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    destinations.forEach { destination ->
                        NavigationBarItem(
                            selected = selected == destination.route,
                            onClick = { onNavigate(destination.route) },
                            icon = { Icon(painterResource(destination.icon), contentDescription = null, modifier = Modifier.size(24.dp)) },
                            label = { Text(stringResource(destination.label)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = WebNavy,
                                selectedTextColor = WebNavy,
                                indicatorColor = WebGold,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        ) { padding ->
            SkyBackground(Modifier.padding(padding)) { content(PaddingValues(0.dp)) }
        }
    }
}
