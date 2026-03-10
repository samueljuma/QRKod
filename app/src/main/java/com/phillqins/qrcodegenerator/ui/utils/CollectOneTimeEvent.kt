package com.phillqins.qrcodegenerator.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

/**
 * This function is used to collect one time events from a flow.
 * @param flow The flow to collect from.
 * @param key1 The first key to use for the LaunchedEffect.
 * @param key2 The second key to use for the LaunchedEffect.
 * @param onEvent The function to execute when an event is collected.
 */
@Composable
fun <T> CollectOneTimeEvent(
    flow: Flow<T>,
    key1: Any? = null,
    key2: Any? = null,
    onEvent: suspend (T) -> Unit
){
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(
        key1 = key1,
        key2 = key2,
        key3 = lifecycleOwner
    ) {
        lifecycleOwner.repeatOnLifecycle( Lifecycle.State.STARTED){
            withContext( Dispatchers.Main.immediate){
                flow.collectLatest(onEvent)
            }
        }
    }
}