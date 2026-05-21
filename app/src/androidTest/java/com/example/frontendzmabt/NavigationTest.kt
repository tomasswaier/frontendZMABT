package com.example.frontendzmabt

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.frontendzmabt.ui.screens.GetNavHost
import junit.framework.TestCase.assertEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController

    @Before
    fun setup() {
        navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )

        navController.navigatorProvider.addNavigator(
            ComposeNavigator()
        )
    }

    @Test
    fun all_navigation_items_are_displayed() {

        composeTestRule.setContent {
            GetNavHost(navController, "main")
        }

        composeTestRule
            .onNodeWithContentDescription("FEED")
            .assertExists()

        composeTestRule
            .onNodeWithContentDescription("MAP")
            .assertExists()

        composeTestRule
            .onNodeWithContentDescription("PROFILE")
            .assertExists()
    }

    @Test
    fun clicking_feed_navigates_to_home_screen() {

        composeTestRule.setContent {
            GetNavHost(navController, "main")
        }

        composeTestRule
            .onNodeWithContentDescription("FEED")
            .performClick()

        assertEquals(
            "home_screen",
            navController.currentBackStackEntry
                ?.destination
                ?.route
        )
    }

    @Test
    fun clicking_map_navigates_to_map_screen() {

        composeTestRule.setContent {
            GetNavHost(navController, "main")
        }

        composeTestRule
            .onNodeWithContentDescription("MAP")
            .performClick()

        assertEquals(
            "map_screen",
            navController.currentBackStackEntry
                ?.destination
                ?.route
        )
    }

    @Test
    fun clicking_profile_navigates_to_profile_screen() {

        composeTestRule.setContent {
            GetNavHost(navController, "main")
        }

        composeTestRule
            .onNodeWithContentDescription("PROFILE")
            .performClick()

        assertEquals(
            "user_profile_screen",
            navController.currentBackStackEntry
                ?.destination
                ?.route
        )
    }

    @Test
    fun feed_text_is_displayed() {

        composeTestRule.setContent {
            GetNavHost(navController, "main")
        }

        composeTestRule
            .onNodeWithText("FEED")
            .assertExists()
    }

    @Test
    fun map_text_is_displayed() {

        composeTestRule.setContent {
            GetNavHost(navController, "main")
        }

        composeTestRule
            .onNodeWithText("MAP")
            .assertExists()
    }

    @Test
    fun profile_text_is_displayed() {

        composeTestRule.setContent {
            GetNavHost(navController, "main")
        }

        composeTestRule
            .onNodeWithText("PROFILE")
            .assertExists()
    }

    @Test
    fun map_becomes_current_route_after_click() {

        composeTestRule.setContent {
            GetNavHost(navController, "main")
        }

        composeTestRule
            .onNodeWithContentDescription("MAP")
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(
                "map_screen",
                navController.currentDestination?.route
            )
        }
    }

    @Test
    fun profile_becomes_current_route_after_click() {

        composeTestRule.setContent {
            GetNavHost(navController, "main")
        }

        composeTestRule
            .onNodeWithContentDescription("PROFILE")
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(
                "user_profile_screen",
                navController.currentDestination?.route
            )
        }
    }

    @Test
    fun repeated_navigation_clicks_test() {

        composeTestRule.setContent {
            GetNavHost(navController, "main")
        }

        composeTestRule
            .onNodeWithContentDescription("PROFILE")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("FEED")
            .performClick()
        composeTestRule
            .onNodeWithContentDescription("MAP")
            .performClick()
        composeTestRule
            .onNodeWithContentDescription("FEED")
            .performClick()
        composeTestRule
            .onNodeWithContentDescription("MAP")
            .performClick()
        composeTestRule
            .onNodeWithContentDescription("FEED")
            .performClick()
        composeTestRule
            .onNodeWithContentDescription("MAP")
            .performClick()
        composeTestRule
            .onNodeWithContentDescription("PROFILE")
            .performClick()
        composeTestRule
            .onNodeWithContentDescription("MAP")
            .performClick()
        composeTestRule.runOnIdle {
            assertEquals(
                "map_screen",
                navController.currentDestination?.route
            )
        }
    }
}
