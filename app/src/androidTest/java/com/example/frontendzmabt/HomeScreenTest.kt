package com.example.frontendzmabt

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.frontendzmabt.ui.components.PostList
import com.example.frontendzmabt.ui.screens.GetNavHost
import junit.framework.TestCase.assertEquals
import com.example.frontendzmabt.data.repository.testRepository.TestPostRepository
import com.example.frontendzmabt.ui.screens.test.TestGetNavHost
import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import com.example.frontendzmabt.ui.screens.Screen
import com.example.frontendzmabt.ui.screens.main.PostScreen
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
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController
    private lateinit var context: Context

    @Before
    fun setup() {
        context=ApplicationProvider.getApplicationContext()
        navController = TestNavHostController(
            context=context
        )

        navController.navigatorProvider.addNavigator(ComposeNavigator())


    }


    @Test
    fun post_click_redirects_to_post_screen() {

        val fakeRepo = TestPostRepository()

        composeTestRule.setContent {
            GetNavHost(navController,"main")
            PostList(
                navController = navController,
                repository = fakeRepo,
                id = 1,
                placeId = 1,
                isUser = false
            )
        }

        composeTestRule
            .onNodeWithText("Post 1")
            .performClick()

        assertEquals(//just some post doesn't matter what post
            "post_screen?postId={postId}&isUser={isUser}",
            navController.currentBackStackEntry?.destination?.route
        )
        assertEquals(
            1,
            navController.currentBackStackEntry
                ?.arguments
                ?.getInt("postId")
        )
    }

    @Test
    fun correct_text_displayed_post_screen() {

        val fakeRepo = TestPostRepository()

        composeTestRule.setContent {
            GetNavHost(navController,"main")
            PostList(
                navController = navController,
                repository = fakeRepo,
                id = 1,
                placeId = 1,
                isUser = false
            )
        }
        composeTestRule
            .onNodeWithText("Post 1")
            .assertIsDisplayed()

    }
    @Test
    fun loads_1000_posts() {

        val fakeRepo = TestPostRepository()

        composeTestRule.setContent {

            PostList(
                navController = navController,
                repository = fakeRepo,
                id = 1,
                placeId = 1,
                isUser = false
            )
        }
        composeTestRule
            .onNodeWithTag("post_list")
            .performScrollToNode(
                hasText("Post 999")
            )
        composeTestRule
            .onNodeWithText("Post 999")
            .assertExists()
    }

}
