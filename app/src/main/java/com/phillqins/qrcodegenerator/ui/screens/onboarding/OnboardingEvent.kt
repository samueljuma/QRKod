package com.phillqins.qrcodegenerator.ui.screens.onboarding

sealed interface OnboardingEvent {
    data object OnClickContinue: OnboardingEvent
}