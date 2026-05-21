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
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import com.example.frontendzmabt.data.SessionManager
import com.example.frontendzmabt.data.repository.testRepository.TestOfflinePostRepository
import com.example.frontendzmabt.ui.screens.Screen
import com.example.frontendzmabt.ui.screens.main.PostCreateScreen
import com.example.frontendzmabt.ui.screens.main.PostCreateScreenContent
import com.example.frontendzmabt.ui.screens.main.PostScreen
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
class PostCreateScreenTest {

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
    fun create_post_header_is_displayed() {

        composeTestRule.setContent {
            GetNavHost(navController, "main")

            navController.navigate(Screen.PostCreateScreen.route)
        }

        composeTestRule
            .onNodeWithText("Create New Post")
            .assertIsDisplayed()
    }

    @Test
    fun story_section_is_displayed() {

        composeTestRule.setContent {
            GetNavHost(navController, "main")

            navController.navigate(Screen.PostCreateScreen.route)
        }

        composeTestRule
            .onNodeWithText("THE STORY")
            .assertIsDisplayed()

    }

    @Test
    fun story_input_is_displayed() {

        composeTestRule.setContent {
            GetNavHost(navController, "main")

            navController.navigate(Screen.PostCreateScreen.route)
        }

        composeTestRule
            .onNodeWithTag("the_story_text_field")
            .assertIsDisplayed()
    }

    @Test
    fun add_photos_section_is_displayed() {

        composeTestRule.setContent {
            GetNavHost(navController, "main")

            navController.navigate(Screen.PostCreateScreen.route)
        }

        composeTestRule
            .onNodeWithText("ADD PHOTOS")
            .assertIsDisplayed()
    }

    @Test
    fun weather_button_is_displayed() {

        composeTestRule.setContent {
            GetNavHost(navController, "main")

            navController.navigate(Screen.PostCreateScreen.route)
        }

        composeTestRule
            .onNodeWithText("Add Current Wether")
            .assertIsDisplayed()
    }

    @Test
    fun location_section_is_displayed() {

        composeTestRule.setContent {
            GetNavHost(navController, "main")

            navController.navigate(Screen.PostCreateScreen.route)
        }

        composeTestRule
            .onNodeWithText("LOCATION")
            .assertIsDisplayed()
    }

    @Test
    fun pick_location_button_is_displayed() {

        composeTestRule.setContent {
            GetNavHost(navController, "main")

            navController.navigate(Screen.PostCreateScreen.route)
        }

        composeTestRule
            .onNodeWithText("Pick a location")
            .assertIsDisplayed()
    }

    @Test
    fun rating_section_is_displayed() {

        composeTestRule.setContent {
            GetNavHost(navController, "main")

            navController.navigate(Screen.PostCreateScreen.route)
        }

        composeTestRule
            .onNodeWithText("RATING")
            .assertIsDisplayed()
    }

    @Test
    fun publish_button_is_displayed() {

        composeTestRule.setContent {
            GetNavHost(navController, "main")

            navController.navigate(Screen.PostCreateScreen.route)
        }

        composeTestRule
            .onNodeWithText("Publish to Trail  ▷")
            .assertIsDisplayed()
    }

    @Test
    fun user_can_type_story() {

        composeTestRule.setContent {
            GetNavHost(navController, "main")

            navController.navigate(Screen.PostCreateScreen.route)
        }

        composeTestRule
            .onNode(
                hasSetTextAction()
            )
            .performTextInput("Test ... tests")

        composeTestRule
            .onNodeWithText("Test ... tests")
            .assertExists()
    }

    @Test
    fun publish_without_location_shows_error() {

        composeTestRule.setContent {
            GetNavHost(navController, "main")

            navController.navigate(Screen.PostCreateScreen.route)
        }

        composeTestRule
            .onNodeWithText("Publish to Trail  ▷")
            .performClick()
        assertEquals(
            "post_create_screen",
            navController.currentBackStackEntry?.destination?.route
        )//if I stay on screen then that means I was stopped from posting bcs no location
    }

    @Test
    fun map_button_opens_location_picker() {

        composeTestRule.setContent {
            GetNavHost(navController, "main")

            navController.navigate(Screen.PostCreateScreen.route)
        }

        composeTestRule
            .onNodeWithText("Pick a location")
            .performClick()

        composeTestRule
            .waitForIdle()
    }
    @Test
    fun edit_screen_prefills_post_text_and_rating() {

        composeTestRule.setContent {
            PostCreateScreenContent(
                navController = navController,
                postId = 1,

                postText = "Test Cached Post 1",
                rating = 5,

                imageUri = null,
                latitude = 48.0,
                longitude = 17.0,
                online = true,

                onPostTextChange = {},
                onRatingChange = {},
                onImageChange = {},
                onSubmit = {}
            )
        }

        composeTestRule
            .onNodeWithText("Test Cached Post 1")
            .assertExists()

        composeTestRule
            .onNodeWithText("Publish to Trail  ▷")
            .assertIsDisplayed()
    }

    @Test
    fun edit_screen_does_not_show_location_section() {

        composeTestRule.setContent {
            PostCreateScreenContent(
                navController = navController,
                postId = 1,

                postText = "Test",
                rating = 3,

                imageUri = null,
                latitude = 48.0,
                longitude = 17.0,
                online = true,

                onPostTextChange = {},
                onRatingChange = {},
                onImageChange = {},
                onSubmit = {}
            )
        }

        composeTestRule
            .onNodeWithText("LOCATION")
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText("Pick a location")
            .assertDoesNotExist()
    }

    @Test
    fun edit_screen_does_not_show_photo_section() {

        composeTestRule.setContent {
            PostCreateScreenContent(
                navController = navController,
                postId = 1,

                postText = "Test",
                rating = 3,

                imageUri = null,
                latitude = 48.0,
                longitude = 17.0,
                online = true,

                onPostTextChange = {},
                onRatingChange = {},
                onImageChange = {},
                onSubmit = {}
            )
        }

        composeTestRule
            .onNodeWithText("ADD PHOTOS")
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText("Add Current Wether")
            .assertDoesNotExist()
    }


    @Test
    fun edit_screen_story_section_is_visible() {

        composeTestRule.setContent {
            PostCreateScreenContent(
                navController = navController,
                postId = 1,

                postText = "Something",
                rating = 3,

                imageUri = null,
                latitude = 0.0,
                longitude = 0.0,
                online = true,

                onPostTextChange = {},
                onRatingChange = {},
                onImageChange = {},
                onSubmit = {}
            )
        }

        composeTestRule
            .onNodeWithText("THE STORY")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("the_story_text_field")
            .assertIsDisplayed()
    }
    @Test
    fun edit_screen_story_input_is_visible() {

        composeTestRule.setContent {
            PostCreateScreenContent(
                navController = navController,
                postId = 1,

                postText = "Something",
                rating = 3,

                imageUri = null,
                latitude = 0.0,
                longitude = 0.0,
                online = true,

                onPostTextChange = {},
                onRatingChange = {},
                onImageChange = {},
                onSubmit = {}
            )
        }


        composeTestRule
            .onNodeWithTag("the_story_text_field")
            .assertIsDisplayed()
    }


    @Test
    fun edit_screen_rating_is_visible() {

        composeTestRule.setContent {
            PostCreateScreenContent(
                navController = navController,
                postId = 1,

                postText = "Test",
                rating = 4,

                imageUri = null,
                latitude = 0.0,
                longitude = 0.0,
                online = true,

                onPostTextChange = {},
                onRatingChange = {},
                onImageChange = {},
                onSubmit = {}
            )
        }

        composeTestRule
            .onNodeWithText("RATING")
            .assertIsDisplayed()
    }


    @Test
    fun edit_screen_submit_button_exists() {

        composeTestRule.setContent {
            PostCreateScreenContent(
                navController = navController,
                postId = 1,

                postText = "Test",
                rating = 3,

                imageUri = null,
                latitude = 0.0,
                longitude = 0.0,
                online = true,

                onPostTextChange = {},
                onRatingChange = {},
                onImageChange = {},
                onSubmit = {}
            )
        }

        composeTestRule
            .onNodeWithText("Publish to Trail  ▷")
            .assertIsDisplayed()
    }


    @Test
    fun edit_screen_map_is_not_rendered() {

        composeTestRule.setContent {
            PostCreateScreenContent(
                navController = navController,
                postId = 1,

                postText = "Test",
                rating = 3,

                imageUri = null,
                latitude = 0.0,
                longitude = 0.0,
                online = true,

                onPostTextChange = {},
                onRatingChange = {},
                onImageChange = {},
                onSubmit = {}
            )
        }

        composeTestRule
            .onNodeWithText("LOCATION")
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText("Pick a location")
            .assertDoesNotExist()
    }
}
