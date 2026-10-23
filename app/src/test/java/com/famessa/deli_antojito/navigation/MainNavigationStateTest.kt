package com.famessa.deli_antojito.navigation

import org.junit.Assert.*
import org.junit.Test

class MainNavigationStateTest {
    @Test fun orderEditReturnsToDetailAndThenList() {
        val state = MainNavigationState()
        state.section("orders")
        state.navigate("detail:25")
        state.navigate("form:25:entry")
        assertTrue(state.destination is MainDestination.OrderForm)
        state.back()
        assertEquals(MainDestination.OrderDetail(25), state.destination)
        state.back()
        assertEquals(MainDestination.Orders, state.destination)
        val restored = MainNavigationState(state.stack)
        restored.back()
        assertEquals(MainDestination.Home, restored.destination)
    }
    @Test fun malformedIdentityFallsBackAndRootBackIsSafe() {
        assertEquals(MainDestination.Orders, decodeDestination("detail:bad"))
        assertEquals(MainDestination.Home, decodeDestination("unknown"))
        val state = MainNavigationState(emptyList())
        state.back()
        assertEquals(listOf("home"), state.stack)
    }
}
