package com.example.frontendzmabt

import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.frontendzmabt.data.repository.validateLogin
import com.example.frontendzmabt.data.repository.validateRegister
import com.example.frontendzmabt.ui.screens.GetNavHost
import com.example.frontendzmabt.ui.screens.NavigationManager
import com.example.frontendzmabt.ui.screens.auth.LoginScreen
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginValidationTest {
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

        composeTestRule.setContent {
            GetNavHost(navController,"auth")
        }
    }
    @Test
    fun login_emptyParams_returnsFalse() {
        val result = validateLogin("", "")
        TestCase.assertFalse(result)
    }
    @Test
    fun login_emptyUsername_returnsFalse() {
        val result= validateLogin("", "somePassword")
        TestCase.assertFalse(result)
    }
    @Test
    fun login_emptyPassword_returnsFalse() {
        val result= validateLogin("someusername", "")
        TestCase.assertFalse(result)
    }


    @Test
    fun login_validInput_returnsTrue() {
        val result = validateLogin("FeroPalka", "1234")
        TestCase.assertTrue(result)
    }

    @Test
    fun register_emptyParams_returnsFalse() {
        val result = validateRegister("", "", "", "")
        TestCase.assertFalse(result)
    }
    @Test
    fun register_emptyUsername_returnsFalse() {
        val result= validateRegister("", "somePassword", "somePassword", "myemail@gmail.com")
        TestCase.assertFalse(result)
    }
    @Test
    fun register_emptyPassword_returnsFalse() {
        val result= validateRegister("someusername", "", "", "myemail@gmail.com")
        TestCase.assertFalse(result)
    }
    @Test
    fun register_shortUsername_returnsFalse() {
        val result= validateRegister("s", "111", "111", "myemail@gmail.com")
        TestCase.assertFalse(result)
    }

    @Test
    fun guest_button_redirects(){

        composeTestRule
            .onNodeWithText("Guest")
            .performClick()

        assertEquals(
            "home_screen",
            navController.currentBackStackEntry?.destination?.route
        )
    }
    @Test
    fun register_button_redirects(){

        composeTestRule
            .onNodeWithText("Register new account")
            .performClick()

        assertEquals(
            "register_screen",
            navController.currentBackStackEntry?.destination?.route
        )
    }
}