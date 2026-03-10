package com.phillqins.qrcodegenerator.ui.screens.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class OnboardingViewModel: ViewModel() {
    var state by mutableStateOf(OnboardingUiState())

    private val _eventChannel = Channel<OnboardingEvent>()
    val event = _eventChannel.receiveAsFlow()

    fun onAction(action: OnboardingAction) {
        when(action) {
            OnboardingAction.OnClickContinue -> continueToHome()
        }

    }
    private fun continueToHome() {
        viewModelScope.launch {
            _eventChannel.send(OnboardingEvent.OnClickContinue)
        }
    }

}