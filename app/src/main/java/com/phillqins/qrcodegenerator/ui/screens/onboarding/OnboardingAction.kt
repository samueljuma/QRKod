package com.phillqins.qrcodegenerator.ui.screens.onboarding

sealed interface OnboardingAction {
    data object OnClickContinue: OnboardingAction
}