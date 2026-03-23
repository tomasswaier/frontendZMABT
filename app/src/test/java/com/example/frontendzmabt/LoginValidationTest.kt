package com.example.frontendzmabt;

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.example.frontendzmabt.data.repository.AuthRepository
import com.example.frontendzmabt.data.repository.validateLogin
import com.example.frontendzmabt.data.repository.validateRegister
import com.example.frontendzmabt.ui.screens.Screen
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test


class LoginValidationTest {

    @Test
    fun login_emptyParams_returnsFalse() {
        val result = validateLogin("", "")
        assertFalse(result)
    }
    @Test
    fun login_emptyUsername_returnsFalse() {
        val result= validateLogin("", "somePassword")
        assertFalse(result)
    }
    @Test
    fun login_emptyPassword_returnsFalse() {
        val result= validateLogin("someusername", "")
        assertFalse(result)
    }


    @Test
    fun login_validInput_returnsTrue() {
        val result = validateLogin("FeroPalka", "1234")
        assertTrue(result)
    }

    @Test
    fun register_emptyParams_returnsFalse() {
        val result = validateRegister("", "","","")
        assertFalse(result)
    }
    @Test
    fun register_emptyUsername_returnsFalse() {
        val result= validateRegister("", "somePassword","somePassword","myemail@gmail.com")
        assertFalse(result)
    }
    @Test
    fun register_emptyPassword_returnsFalse() {
        val result= validateRegister("someusername", "","","myemail@gmail.com")
        assertFalse(result)
    }
    @Test
    fun register_shortUsername_returnsFalse() {
        val result= validateRegister("s", "111","111","myemail@gmail.com")
        assertFalse(result)
    }
}
