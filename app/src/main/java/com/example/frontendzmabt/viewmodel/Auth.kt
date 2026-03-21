package com.example.frontendzmabt.viewmodel

import android.content.Context
import android.se.omapi.Session
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.example.frontendzmabt.BuildConfig
import com.example.frontendzmabt.data.API
import com.example.frontendzmabt.data.SessionManager
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Data classes for the request and response