package com.example.frontendzmabt.data

import com.example.frontendzmabt.BuildConfig
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

object SocketManager {

    private lateinit var socket: Socket

    fun init(token: String) {
        val options = IO.Options()
        options.auth = mapOf("token" to token)
        socket = IO.socket(BuildConfig.BACKEND_API_URL, options)
        socket.connect()
    }

    fun connect() { socket.connect() }
    fun disconnect() { socket.disconnect() }
    fun getSocket(): Socket = socket

    fun sendComment(postId: Int, commentText: String): Boolean {
        val data = JSONObject()
        data.put("postId", postId)
        data.put("content", commentText)
        socket.emit("saveComment", data.toString())
        return true
    }

    fun joinPost(postId: Int) {
        socket.emit("joinPost", postId)
    }
}
