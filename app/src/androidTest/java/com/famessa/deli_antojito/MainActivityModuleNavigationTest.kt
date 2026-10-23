package com.famessa.deli_antojito

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test

class MainActivityModuleNavigationTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun modulesStayOpenFromHomeCardsAndBottomNavigation() {
        compose.waitUntil(10_000) {
            exists("home_screen") || exists("business_config_screen")
        }
        assumeTrue("Requires a configured business on the test device", exists("home_screen"))

        for ((label, route, screen) in listOf(
            Triple("Productos", "products", "product_list_screen"),
            Triple("Pedidos", "orders", "orders_screen")
        )) {
            compose.onNode(hasText(label) and hasAnyAncestor(hasTestTag("home_screen")))
                .performScrollTo().performClick()
            assertModuleStaysOpen(screen)
            compose.onNodeWithTag("nav_home").performClick()
            compose.onNodeWithTag("home_screen").assertIsDisplayed()

            compose.onNodeWithTag("nav_$route").performClick()
            assertModuleStaysOpen(screen)
            compose.activityRule.scenario.recreate()
            assertModuleStaysOpen(screen)
            compose.onNodeWithTag("nav_home").performClick()
        }
    }

    private fun assertModuleStaysOpen(screen: String) {
        compose.waitUntil(10_000) { exists(screen) }
        compose.waitForIdle()
        compose.onNodeWithTag(screen).assertIsDisplayed()
        compose.onNodeWithTag("home_screen").assertDoesNotExist()
        compose.onNodeWithTag("startup_loading").assertDoesNotExist()
    }

    private fun exists(tag: String): Boolean =
        compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
}
