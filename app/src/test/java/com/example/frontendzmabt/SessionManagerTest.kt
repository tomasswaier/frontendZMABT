package com.example.frontendzmabt

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.frontendzmabt.data.SessionManager
import com.example.frontendzmabt.data.dataStore
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SessionManagerTest {

    private lateinit var context: Context
    private lateinit var sessionManager: SessionManager

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

        assertEquals("abc123", token)
    }

    @Test
    fun getToken_withoutSaving_returnsNull() = runTest {
        val token = sessionManager.getToken()

        assertNull(token)
    }

    @Test
    fun getUser_returnsCorrectData() = runTest {
        sessionManager.saveToken("token", "john", "john@mail.com", 5)

        val user = sessionManager.getUser()

        assertEquals(5, user.id)
        assertEquals("john", user.username)
        assertEquals("john@mail.com", user.email)
    }

    @Test
    fun getUser_withoutData_returnsNullFields() = runTest {
        val user = sessionManager.getUser()

        assertNull(user.id)
        assertNull(user.username)
        assertNull(user.email)
    }

    @Test
    fun isLoggedIn_returnsTrue_whenTokenExists() = runTest {
        sessionManager.saveToken("token", "john", "mail", 1)

        val result = sessionManager.isLoggedIn()

        assertTrue(result)
    }

    @Test
    fun isLoggedIn_returnsFalse_whenNoToken() = runTest {
        val result = sessionManager.isLoggedIn()

        assertFalse(result)
    }

    @Test
    fun logout_clearsToken() = runTest {
        sessionManager.saveToken("token", "john", "mail", 1)

        sessionManager.logout()

        val token = sessionManager.getToken()

        assertNull(token)
    }

    @Test
    fun logout_clearsUserData() = runTest {
        sessionManager.saveToken("token", "john", "mail", 1)

        sessionManager.logout()

        val user = sessionManager.getUser()

        assertNull(user.id)
        assertNull(user.username)
        assertNull(user.email)
    }
}