package com.example.frontendzmabt.data

import com.example.frontendzmabt.BuildConfig
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

object SocketManager {
    //Socker manager vygenerovla chat

    private lateinit var socket: Socket

    fun init(token: String) {

        val options = IO.Options()
        options.auth = mapOf("token" to token)


        println(token)
        socket = IO.socket(BuildConfig.BACKEND_API_URL,options);
        socket.connect()
        println("SocketManager init")


    }

    fun connect() {
        socket.connect()
    }

    fun disconnect() {
        socket.disconnect()
    }

    fun getSocket(): Socket {
        return socket
    }
    fun sendComment(postId: Int, commentText: String): Boolean {

        val data = mapOf(
            "postId" to postId,
            "content" to commentText,
            //put("commentId", JSONObject.NULL) // for replies later
        );


        socket.emit("saveComment", JSONObject(data).toString())
        return true
    }
    fun joinPost(postId:Int) {
        socket.emit("joinPost", postId)
    }

    /*// 👂 LISTEN FOR COMMENTS
    socket.on("newComment") { args ->
        val data = args[0] as JSONObject
        println("New comment: $data")

        // 👉 update UI state here
    }*/
}
