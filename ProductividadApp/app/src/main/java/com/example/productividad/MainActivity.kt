package com.example.productividad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.productividad.data.*
import com.example.productividad.ui.components.RifitLogo
import com.example.productividad.ui.screens.*
import com.example.productividad.ui.theme.ProductividadAppTheme
import com.example.productividad.viewmodel.GymViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        setContent {
            val vm: GymViewModel = viewModel()
            ProductividadAppTheme(darkTheme = vm.isDarkTheme, dynamicColor = false) { 
                GymApp(vm) 
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GymApp(vm: GymViewModel) {
    val t: (String, String) -> String = { es, en -> vm.tr(es, en) }
    val navSelectedContentColor = if (vm.isDarkTheme) Color.White else AppColors.TEXT
    val navSelectedIndicatorColor =
        if (vm.isDarkTheme) AppColors.ACCENT else AppColors.ACCENT.copy(alpha = 0.16f)
    if (!vm.hasCompletedOnboarding) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(AppColors.BG, AppColors.BG_SOFT, AppColors.BG)))
        ) {
            OnboardingScreen(vm, t)
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.BG,
                    titleContentColor = AppColors.TEXT
                ),
                title = {
                    val title = when (vm.selectedTab) {
                        GymTab.HOME -> "RIFIT"
                        GymTab.ROUTINES -> t("Rutinas", "Routines")
                        GymTab.TRAIN -> t("Entrenar", "Train")
                        GymTab.NOTES -> t("Notas", "Notes")
                        GymTab.CARDIO -> t("Cardio", "Cardio")
                        GymTab.PROFILE -> t("Perfil", "Profile")
                    }
                    Row {
                        RifitLogo(
                            modifier = Modifier.size(36.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .border(1.dp, AppColors.BORDER, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
                color = AppColors.CARD,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    tonalElevation = 0.dp,
                    modifier = Modifier.height(88.dp),
                    windowInsets = WindowInsets(0, 0, 0, 0)
                ) {
                    GymTab.values().forEach { tab ->
                        val icon = when (tab) {
                            GymTab.HOME -> Icons.Filled.Home
                            GymTab.ROUTINES -> Icons.AutoMirrored.Filled.ViewList
                            GymTab.TRAIN -> Icons.Filled.PlayCircle
                            GymTab.NOTES -> Icons.Filled.Description
                            GymTab.CARDIO -> Icons.Filled.Timer
                            GymTab.PROFILE -> Icons.Filled.Person
                        }
                        NavigationBarItem(
                            selected = vm.selectedTab == tab,
                            onClick = { vm.selectedTab = tab },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = navSelectedContentColor,
                                selectedTextColor = navSelectedContentColor,
                                indicatorColor = navSelectedIndicatorColor,
                                unselectedIconColor = AppColors.TEXT_SECONDARY,
                                unselectedTextColor = AppColors.TEXT_SECONDARY
                            ),
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = if (vm.language == AppLanguage.ES) tab.es else tab.en,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    if (vm.language == AppLanguage.ES) tab.es else tab.en,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.2.sp
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(AppColors.BG, AppColors.BG_SOFT, AppColors.BG)))
                .padding(padding)
        ) {
            when (vm.selectedTab) {
                GymTab.HOME -> HomeScreen(vm, t)
                GymTab.ROUTINES -> RoutinesScreen(vm, t)
                GymTab.TRAIN -> TrainScreen(vm, t)
                GymTab.NOTES -> NotesScreen(vm, t)
                GymTab.CARDIO -> CardioScreen(vm, t)
                GymTab.PROFILE -> ProfileScreen(vm, t)
            }
        }
    }
}
