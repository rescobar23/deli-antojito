package com.famessa.deli_antojito.feature.orders.components

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.StateFlow

private fun Context.lifecycleOwner(): LifecycleOwner? {
    var current: Context = this
    while (true) {
        if (current is LifecycleOwner) return current
        if (current !is ContextWrapper || current.baseContext === current) return null
        current = current.baseContext
    }
}
@Composable fun <T> StateFlow<T>.collectOrderState(): State<T> {
    val owner = LocalContext.current.lifecycleOwner()
    val initial = remember(this) { value }
    return produceState(initial, this, owner) {
        if (owner == null) collect { value = it }
        else owner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) { collect { value = it } }
    }
}
