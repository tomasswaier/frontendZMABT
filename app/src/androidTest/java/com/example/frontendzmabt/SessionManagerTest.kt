package com.example.frontendzmabt

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.datastore.preferences.core.edit
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.frontendzmabt.data.SessionManager
import com.example.frontendzmabt.data.dataStore
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SessionManagerTest {


    private lateinit var context: Context
    private lateinit var sessionManager: SessionManager
    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        runBlocking {
            context.dataStore.edit { it.clear() }
        }

        sessionManager = SessionManager(context)
    }

    @Test
    fun saveToken_andGetToken_returnsCorrectValue() = runTest {
        sessionManager.saveToken("abc123", "john", "john@mail.com", 1)

        val token = sessionManager.getToken()

        TestCase.assertEquals("abc123", token)
    }

    @Test
    fun getToken_withoutSaving_returnsNull() = runTest {
        val token = sessionManager.getToken()

        TestCase.assertNull(token)
    }

    @Test
    fun getUser_returnsCorrectData() = runTest {
        sessionManager.saveToken("token", "john", "john@mail.com", 5)

        val user = sessionManager.getUser()

        TestCase.assertEquals(5, user.id)
        TestCase.assertEquals("john", user.username)
        TestCase.assertEquals("john@mail.com", user.email)
    }

    @Test
    fun getUser_withoutData_returnsNullFields() = runTest {
        val user = sessionManager.getUser()

        TestCase.assertNull(user.id)
        TestCase.assertNull(user.username)
        TestCase.assertNull(user.email)
    }

    @Test
    fun isLoggedIn_returnsTrue_whenTokenExists() = runTest {
        sessionManager.saveToken("token", "john", "mail", 1)

        val result = sessionManager.isLoggedIn()

        TestCase.assertTrue(result)
    }

    @Test
    fun isLoggedIn_returnsFalse_whenNoToken() = runTest {
        val result = sessionManager.isLoggedIn()

        TestCase.assertFalse(result)
    }

    @Test
    fun logout_clearsToken() = runTest {
        sessionManager.saveToken("token", "john", "mail", 1)

        sessionManager.logout()

        val token = sessionManager.getToken()

        TestCase.assertNull(token)
    }

    @Test
    fun logout_clearsUserData() = runTest {
        sessionManager.saveToken("token", "john", "mail", 1)

        sessionManager.logout()

        val user = sessionManager.getUser()

        TestCase.assertNull(user.id)
        TestCase.assertNull(user.username)
        TestCase.assertNull(user.email)
    }
}