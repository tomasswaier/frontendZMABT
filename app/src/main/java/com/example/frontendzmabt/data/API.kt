package com.example.frontendzmabt.data

import android.util.Log
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class API {

    companion object {
        suspend fun callApi(apiUrl: String, token: String, httpMethod: String, requestModel: Any? = null): String {
            val response = StringBuilder()
            try {
                val connection = URL(apiUrl).openConnection() as HttpURLConnection
                connection.requestMethod = httpMethod
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                connection.setRequestProperty("Accept", "application/json")
                connection.setRequestProperty("Authorization", "Bearer $token")

                if (httpMethod == "POST" || httpMethod == "PUT" || httpMethod == "PATCH") {
                    connection.doOutput = true
                    requestModel?.let {
                        OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { os ->
                            os.write(Gson().toJson(it))
                            os.flush()
                        }
                    }
                }

                val responseCode = connection.responseCode
                val stream = if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    connection.inputStream
                } else {
                    Log.e("API", "HTTP $responseCode: ${connection.responseMessage} — $apiUrl")
                    connection.errorStream
                }
                BufferedReader(InputStreamReader(stream, "utf-8")).use { br ->
                    var line: String?
                    while (br.readLine().also { line = it } != null) {
                        response.append(line?.trim())
                    }
                }
            } catch (e: Exception) {
                Log.e("API", "callApi failed: ${e.message}", e)
                return e.message.toString()
            }
            return response.toString()
        }
    }
}