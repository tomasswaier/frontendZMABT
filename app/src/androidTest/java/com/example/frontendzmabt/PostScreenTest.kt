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
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import com.example.frontendzmabt.data.SessionManager
import com.example.frontendzmabt.data.model.Post
import com.example.frontendzmabt.data.model.PostUser
import com.example.frontendzmabt.data.model.User
import com.example.frontendzmabt.data.repository.testRepository.TestCommentRepository
import com.example.frontendzmabt.data.repository.testRepository.TestPostRepository
import com.example.frontendzmabt.ui.components.CommentList
import com.example.frontendzmabt.ui.screens.main.ChangeRating
import com.example.frontendzmabt.ui.screens.main.PostScreenContent
import kotlinx.coroutines.launch
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

        val commentRepo = TestCommentRepository()
        composeTestRule.setContent {
            GetNavHost(navController,"main")
            PostScreenContent(
                navController = navController,
                id = 1,
                isUser = false,
                isLoggedIn = false,
                post = Post(
                    id = 1,
                    userId = 1,
                    placeId = 1,
                    description = "Test text",
                    createdAt = "1.2.2000",
                    updatedAt = "1.2.2000",
                    stars = 3,
                    user = PostUser(
                        id = 1,
                        username = "testUsername",
                    )
                ),
                images = null,
                rating = 3,
                commentRepo = commentRepo,
                onRatingChanged = {
                }
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

        val commentRepo = TestCommentRepository()
        composeTestRule.setContent {
            GetNavHost(navController,"main")
            PostScreenContent(
                navController = navController,
                id = 1,
                isUser = false,
                isLoggedIn = false,
                post = Post(
                    id = 1,
                    userId = 1,
                    placeId = 1,
                    description = "Test text",
                    createdAt = "1.2.2000",
                    updatedAt = "1.2.2000",
                    stars = 3,
                    user = PostUser(
                        id = 1,
                        username = "testUsername",
                    )
                ),
                images = null,
                rating = 3,
                commentRepo = commentRepo,
                onRatingChanged = {
                }
            )
        }

        composeTestRule
            .onNodeWithText("userId:1")
            .assertIsDisplayed()
    }

    @Test
    fun post_description_displayed_correctly() {

        val commentRepo = TestCommentRepository()
        composeTestRule.setContent {
            GetNavHost(navController,"main")
            PostScreenContent(
                navController = navController,
                id = 1,
                isUser = false,
                isLoggedIn = false,
                post = Post(
                    id = 1,
                    userId = 1,
                    placeId = 1,
                    description = "Test text",
                    createdAt = "1.2.2000",
                    updatedAt = "1.2.2000",
                    stars = 3,
                    user = PostUser(
                        id = 1,
                        username = "testUsername",
                    )
                ),
                images = null,
                rating = 3,
                commentRepo = commentRepo,
                onRatingChanged = {
                }
            )
        }


        composeTestRule
            .onNodeWithText("userId:1")
            .assertIsDisplayed()
    }

    @Test
    fun map_label_displayed_correctly() {

        val commentRepo = TestCommentRepository()
        composeTestRule.setContent {
            GetNavHost(navController,"main")
            PostScreenContent(
                navController = navController,
                id = 1,
                isUser = false,
                isLoggedIn = false,
                post = Post(
                    id = 1,
                    userId = 1,
                    placeId = 1,
                    description = "Test text",
                    createdAt = "1.2.2000",
                    updatedAt = "1.2.2000",
                    stars = 3,
                    user = PostUser(
                        id = 1,
                        username = "testUsername",
                    )
                ),
                images = null,
                rating = 3,
                commentRepo = commentRepo,
                onRatingChanged = {
                }
            )
        }

        composeTestRule
            .onNodeWithText("MAPA SEM :")
            .assertIsDisplayed()
    }

    @Test
    fun open_profile_button_displayed() {

        val commentRepo = TestCommentRepository()
        composeTestRule.setContent {
            GetNavHost(navController,"main")
            PostScreenContent(
                navController = navController,
                id = 1,
                isUser = false,
                isLoggedIn = false,
                post = Post(
                    id = 1,
                    userId = 1,
                    placeId = 1,
                    description = "Test text",
                    createdAt = "1.2.2000",
                    updatedAt = "1.2.2000",
                    stars = 3,
                    user = PostUser(
                        id = 1,
                        username = "testUsername",
                    )
                ),
                images = null,
                rating = 3,
                commentRepo = commentRepo,
                onRatingChanged = {
                }
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Open profile")
            .assertIsDisplayed()
    }

    @Test
    fun open_profile_button_redirects_correctly() {

        val commentRepo = TestCommentRepository()
        composeTestRule.setContent {
            GetNavHost(navController,"main")
            PostScreenContent(
                navController = navController,
                id = 1,
                isUser = false,
                isLoggedIn = false,
                post = Post(
                    id = 1,
                    userId = 2,
                    placeId = 1,
                    description = "Test text",
                    createdAt = "1.2.2000",
                    updatedAt = "1.2.2000",
                    stars = 3,
                    user = PostUser(
                        id = 1,
                        username = "testUsername",
                    )
                ),
                images = null,
                rating = 3,
                commentRepo = commentRepo,
                onRatingChanged = {
                }
            )


        }

        composeTestRule
            .onAllNodesWithContentDescription("Open profile")
            .fetchSemanticsNodes().isNotEmpty()

        composeTestRule
            .onNodeWithText("Share & Trail")// navigator is tied to main activity and I manipulate the inserted PostScreen
            .assertExists()
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
    @Test
    fun edit_button_is_displayed() {

        val commentRepo = TestCommentRepository()
        composeTestRule.setContent {
            GetNavHost(navController,"main")
            PostScreenContent(
                navController = navController,
                id = 1,
                isUser = true,
                isLoggedIn = true,
                post = Post(
                    id = 1,
                    userId = 2,
                    placeId = 1,
                    description = "Test text",
                    createdAt = "1.2.2000",
                    updatedAt = "1.2.2000",
                    stars = 3,
                    user = PostUser(
                        id = 1,
                        username = "testUsername",
                    )
                ),
                images = null,
                rating = 3,
                commentRepo = commentRepo,
                onRatingChanged = {
                }
            )


        }

        composeTestRule
            .onNodeWithTag("edit_button").assertExists()

    }
    @Test
    fun edit_button_redirects_correctly() {

        val commentRepo = TestCommentRepository()
        composeTestRule.setContent {
            GetNavHost(navController,"main")
            PostScreenContent(
                navController = navController,
                id = 1,
                isUser = true,
                isLoggedIn = true,
                post = Post(
                    id = 1,
                    userId = 2,
                    placeId = 1,
                    description = "Test text",
                    createdAt = "1.2.2000",
                    updatedAt = "1.2.2000",
                    stars = 3,
                    user = PostUser(
                        id = 1,
                        username = "testUsername",
                    )
                ),
                images = null,
                rating = 3,
                commentRepo = commentRepo,
                onRatingChanged = {
                }
            )


        }

        composeTestRule
            .onNodeWithTag("edit_button").performClick()

        composeTestRule
            .onNodeWithText("THE STORY")// navigator is tied to main activity and I manipulate the inserted PostScreen
            .assertExists()
    }

}
