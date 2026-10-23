package com.famessa.deli_antojito.feature.batchclosing
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle

@Composable fun BatchClosingRoute(onBack: ()->Unit, viewModel: BatchClosingViewModel = hiltViewModel()) {
    val owner=LocalLifecycleOwner.current
    val initialState=remember(viewModel) { viewModel.state.value }
    val state by produceState(initialState, owner, viewModel) {
        owner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) { viewModel.state.collect { value=it } }
    }
    DisposableEffect(owner,viewModel) {
        val observer=LifecycleEventObserver { _,event -> if(event==Lifecycle.Event.ON_RESUME) viewModel.onResume() }
        owner.lifecycle.addObserver(observer)
        onDispose {owner.lifecycle.removeObserver(observer)}
    }
    BatchClosingScreen(state,onBack,viewModel::beginEdit,viewModel::refresh,viewModel::cancelEdit,viewModel::selectDate)
}


