package com.example.frontendzmabt.ui.components

import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.navigation.NavController
import com.example.frontendzmabt.BuildConfig
import com.example.frontendzmabt.data.repository.AuthRepository
import com.example.frontendzmabt.ui.screens.Screen
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

@Composable
fun GoogleLoginButton(navController: NavController) {
    /*vibe coded*/
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Button(
        onClick = {
            scope.launch {
                try {
                    val credentialManager = CredentialManager.create(context)

                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setServerClientId(
                            BuildConfig.GOOGLE_CLIENT_ID
                        )
                        .setFilterByAuthorizedAccounts(false)
                        .build()
                    println(BuildConfig.GOOGLE_CLIENT_ID)


                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    val result = credentialManager.getCredential(
                        request = request,
                        context = context
                    )

                    val credential = result.credential

                    if (credential is CustomCredential &&
                        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                    ) {

                        val googleCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)

                        val idToken = googleCredential.idToken

                        val repo = AuthRepository(context)
                        val success = repo.loginWithGoogle(idToken)

                        if (success) {
                            navController.navigate(Screen.HomeScreen.route)
                        } else {
                            Toast.makeText(context, "Google login failed", Toast.LENGTH_LONG).show()
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        },
        modifier = Modifier.fillMaxWidth().height(50.dp)
    ) {
        Text("Sign in with Google")
    }
}