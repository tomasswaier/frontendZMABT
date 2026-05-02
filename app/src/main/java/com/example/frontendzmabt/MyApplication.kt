package com.example.frontendzmabt

import android.app.Application
import com.example.frontendzmabt.data.API
import com.example.frontendzmabt.data.AppDatabase
import com.example.frontendzmabt.data.SessionManager
import com.example.frontendzmabt.data.repository.PostRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyApplication : Application() {
    val database by lazy { AppDatabase.getInstance(this) }

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                syncPendingPostsIfLoggedIn()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun syncPendingPostsIfLoggedIn() {
        try {
            println("one")
            val isOnline = API().isOnline(this@MyApplication)
            if (!isOnline) return

            println("tw")

            val session = SessionManager(this@MyApplication)
            val token = session.getToken()
            if (token.isNullOrEmpty()) return

            println("three")
            val db = AppDatabase.getInstance(this@MyApplication)
            val pending = db.cachedPostDao().getAll()
            if (pending.isEmpty()) return
            println("four")

            val repo = PostRepository(this@MyApplication)

            println("five")
            for (post in pending) {
                try {
                    println("six")
                    val success = repo.create(
                        postText = post.description,
                        rating = post.stars,
                        longitude = post.longitude,
                        latitude = post.latitude,
                        imageUri = null,
                        online = true
                    )
                    if (success) {
                        println("seven")
                        db.cachedPostDao().delete(post)
                        println("eight")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }}