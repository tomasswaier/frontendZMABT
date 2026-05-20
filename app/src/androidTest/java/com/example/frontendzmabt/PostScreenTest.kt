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
import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import com.example.frontendzmabt.data.SessionManager
import com.example.frontendzmabt.data.repository.testRepository.TestCommentRepository
import com.example.frontendzmabt.ui.components.CommentList
import kotlinx.coroutines.runBlocking
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
class PostScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController
    private lateinit var context: Context

    @Before
    fun setup() {
        runBlocking {
            context = ApplicationProvider.getApplicationContext()


            SessionManager(context).saveToken("testToken", "testUsername", "test@email.com", 1)


            navController = TestNavHostController(
                context = context
            )

            navController.navigatorProvider.addNavigator(ComposeNavigator())
        }


    }



    @Test
    fun pfp_click_redirects_to_user_profile() {

        composeTestRule.setContent {
            GetNavHost(navController,"main")
        }
        composeTestRule.runOnUiThread {
            navController.navigate(
                "post_screen?postId=1&isUser=false"
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Open profile")
            .performClick()

        assertEquals(//just some post doesn't matter what post
            "profile_screen?userId={userId}",
            navController.currentBackStackEntry?.destination?.route
        )
    }




    @Test
    fun post_user_id_displayed_correctly() {

        composeTestRule.setContent {
            GetNavHost(navController,"main")
        }

        composeTestRule.runOnUiThread {
            navController.navigate(
                "post_screen?postId=1&isUser=false"
            )
        }

        composeTestRule
            .onNodeWithText("userId:1")
            .assertIsDisplayed()
    }

    @Test
    fun post_description_displayed_correctly() {

        composeTestRule.setContent {
            GetNavHost(navController,"main")
        }

        composeTestRule.runOnUiThread {
            navController.navigate(
                "post_screen?postId=1&isUser=false"
            )
        }

        composeTestRule
            .onNodeWithText("userId:1")
            .assertIsDisplayed()
    }

    @Test
    fun map_label_displayed_correctly() {

        composeTestRule.setContent {
            GetNavHost(navController,"main")
        }

        composeTestRule.runOnUiThread {
            navController.navigate(
                "post_screen?postId=1&isUser=false"
            )
        }

        composeTestRule
            .onNodeWithText("MAPA SEM :")
            .assertIsDisplayed()
    }

    @Test
    fun open_profile_button_displayed() {

        composeTestRule.setContent {
            GetNavHost(navController,"main")
        }

        composeTestRule.runOnUiThread {
            navController.navigate(
                "post_screen?postId=1&isUser=false"
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Open profile")
            .assertIsDisplayed()
    }

    @Test
    fun open_profile_button_redirects_correctly() {

        composeTestRule.setContent {
            GetNavHost(navController,"main")
        }

        composeTestRule.runOnUiThread {
            navController.navigate(
                "post_screen?postId=1&isUser=false"
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Open profile")
            .performClick()

        assertEquals(
            "profile_screen?userId={userId}",
            navController.currentDestination?.route
        )
    }
    @Test
    fun comment_list_displays_first_comment() {

        val fakeRepo = TestCommentRepository()

        composeTestRule.setContent {
            GetNavHost(navController,"main")
            CommentList(
                navController = navController,
                repo = fakeRepo,
                id = 1
            )
        }

        composeTestRule
            .onNodeWithText("Test Comment 2")
            .assertExists()
    }

    @Test
    fun comment_list_displays_all_comments() {

        val fakeRepo = TestCommentRepository()

        composeTestRule.setContent {
            GetNavHost(navController,"main")
            CommentList(
                navController = navController,
                repo = fakeRepo,
                id = 1
            )
        }

        composeTestRule
            .onAllNodesWithTag("comment_content")
            .assertCountEquals(2)
    }

    @Test
    fun comment_like_icon_is_shown_when_logged_in() {

        val fakeRepo = TestCommentRepository()

        composeTestRule.setContent {
            GetNavHost(navController,"main")
            CommentList(
                navController = navController,
                repo = fakeRepo,
                id = 1
            )
        }

        composeTestRule
            .onAllNodesWithTag("like_button")
            .assertCountEquals(2)
    }

    @Test
    fun comment_text_is_rendered_correctly_for_first_item() {

        val fakeRepo = TestCommentRepository()

        composeTestRule.setContent {
            GetNavHost(navController,"main")
            CommentList(
                navController = navController,
                repo = fakeRepo,
                id = 1
            )
        }

        composeTestRule
            .onNodeWithText("Test Comment 1")
            .assertIsDisplayed()
    }

    @Test
    fun comment_like_state_true_comment_present() {

        val fakeRepo = TestCommentRepository()

        composeTestRule.setContent {
            GetNavHost(navController,"main")
            CommentList(
                navController = navController,
                repo = fakeRepo,
                id = 1
            )
        }

        // first comment has isLiked = true
        composeTestRule
            .onNodeWithText("Test Comment 2")
            .assertExists()
    }
}
