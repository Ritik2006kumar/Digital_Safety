package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnLock
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.feature.ai.AiAssistantScreen
import com.example.ui.feature.apps.AppsScreen
import com.example.ui.feature.dashboard.DashboardScreen
import com.example.ui.feature.network.NetworkScreen
import com.example.ui.feature.permissions.PermissionsScreen
import com.example.ui.feature.pro.ProSecurityCenterScreen
import com.example.ui.feature.reports.ReportsScreen
import com.example.ui.feature.scam.ScamScreen
import com.example.ui.feature.vault.VaultScreen
import com.example.ui.theme.BentoCanvasBg
import com.example.ui.theme.BentoCardBg
import com.example.ui.theme.BentoCardBorder
import com.example.ui.theme.BentoHeroAccent
import com.example.ui.theme.BentoHeroBg
import com.example.ui.theme.BentoHeroText
import com.example.ui.theme.BentoTextMuted
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.viewmodel.AiViewModel
import com.example.ui.viewmodel.AppsViewModel
import com.example.ui.viewmodel.DashboardViewModel
import com.example.ui.viewmodel.NetworkViewModel
import com.example.ui.viewmodel.PermissionsViewModel
import com.example.ui.viewmodel.ProSecurityViewModel
import com.example.ui.viewmodel.ScamViewModel
import com.example.ui.viewmodel.VaultViewModel

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Shield)
    object Apps : Screen("apps", "Apps", Icons.Default.Apps)
    object Permissions : Screen("permissions", "Permissions", Icons.Default.Policy)
    object Scam : Screen("scam", "Scam Shield", Icons.Default.VpnLock)
    object AiAdvisor : Screen("ai_advisor", "ShieldAI", Icons.Default.AutoAwesome)
    object Network : Screen("network", "Network", Icons.Default.Wifi)
    object Vault : Screen("vault", "Vault", Icons.Default.Key)
    object Reports : Screen("reports", "Reports", Icons.Default.Security)
    object ProSecurity : Screen("pro_security/{tab}", "Pro Security", Icons.Default.Security)
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val dashboardViewModel: DashboardViewModel = viewModel()
    val appsViewModel: AppsViewModel = viewModel()
    val permissionsViewModel: PermissionsViewModel = viewModel()
    val networkViewModel: NetworkViewModel = viewModel()
    val scamViewModel: ScamViewModel = viewModel()
    val aiViewModel: AiViewModel = viewModel()
    val vaultViewModel: VaultViewModel = viewModel()
    val proSecurityViewModel: ProSecurityViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        Screen.Dashboard,
        Screen.Apps,
        Screen.Permissions,
        Screen.Scam,
        Screen.AiAdvisor
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BentoCanvasBg,
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .border(androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder))
                    .background(BentoCardBg)
                    .height(72.dp)
                    .navigationBarsPadding(),
                containerColor = BentoCardBg,
                contentColor = BentoHeroText,
                tonalElevation = 0.dp
            ) {
                bottomNavItems.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BentoHeroText,
                            selectedTextColor = BentoHeroText,
                            unselectedIconColor = BentoTextMuted,
                            unselectedTextColor = BentoTextMuted,
                            indicatorColor = BentoHeroBg
                        ),
                        modifier = Modifier.testTag("nav_${screen.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    onNavigateToApps = { navController.navigate(Screen.Apps.route) },
                    onNavigateToPermissions = { navController.navigate(Screen.Permissions.route) },
                    onNavigateToNetwork = { navController.navigate(Screen.Network.route) },
                    onNavigateToScam = { navController.navigate(Screen.Scam.route) },
                    onNavigateToAi = { navController.navigate(Screen.AiAdvisor.route) },
                    onNavigateToVault = { navController.navigate(Screen.Vault.route) },
                    onNavigateToReports = { navController.navigate(Screen.Reports.route) },
                    onNavigateToProSecurity = { tab -> navController.navigate("pro_security/$tab") }
                )
            }

            composable("pro_security/{tab}") { backStackEntry ->
                val tabIndex = backStackEntry.arguments?.getString("tab")?.toIntOrNull() ?: 0
                ProSecurityCenterScreen(
                    viewModel = proSecurityViewModel,
                    initialTab = tabIndex,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("pro_security") {
                ProSecurityCenterScreen(
                    viewModel = proSecurityViewModel,
                    initialTab = 0,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Apps.route) {
                AppsScreen(
                    viewModel = appsViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Permissions.route) {
                PermissionsScreen(
                    viewModel = permissionsViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Scam.route) {
                ScamScreen(
                    viewModel = scamViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AiAdvisor.route) {
                AiAssistantScreen(
                    viewModel = aiViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Network.route) {
                NetworkScreen(
                    viewModel = networkViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Vault.route) {
                VaultScreen(
                    viewModel = vaultViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Reports.route) {
                ReportsScreen(
                    repository = dashboardViewModel.repository,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

