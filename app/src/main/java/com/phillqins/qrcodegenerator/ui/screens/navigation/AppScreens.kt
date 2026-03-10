package com.phillqins.qrcodegenerator.ui.screens.navigation

sealed class AppScreens(val route: String){
    object HomeScreen: AppScreens("home_screen")
    object OnboardingScreen: AppScreens("onboarding_screen")
}