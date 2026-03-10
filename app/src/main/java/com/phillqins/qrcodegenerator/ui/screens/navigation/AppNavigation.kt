package com.phillqins.qrcodegenerator.ui.screens.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.phillqins.qrcodegenerator.ui.screens.onboarding.OnboardingScreenRoot
import com.phillqins.qrcodegenerator.ui.screens.qrcode.QRCodeScreenRoot

@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppScreens.OnboardingScreen.route
    ) {
        composable(AppScreens.OnboardingScreen.route) {
            OnboardingScreenRoot(
                onGotoHome = {
                    navController.navigate(AppScreens.HomeScreen.route){
                        popUpTo(AppScreens.OnboardingScreen.route){
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(AppScreens.HomeScreen.route) {
            QRCodeScreenRoot()
        }
    }
}