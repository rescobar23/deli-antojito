package com.famessa.deli_antojito.navigation
import org.junit.Test
import org.junit.Assert.*
class BatchClosingNavigationStateTest {
    @Test fun closeIsDistinctAndSaveable() {
        val state=MainNavigationState(); state.section("close")
        assertEquals(MainDestination.BatchClosing,state.destination)
        val restored=MainNavigationState(state.stack); assertEquals(MainDestination.BatchClosing,restored.destination)
        restored.back(); assertEquals(MainDestination.Home,restored.destination)
    }
}
