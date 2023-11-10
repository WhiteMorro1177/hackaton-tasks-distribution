package ru.alt.tasksdistribution.requests

import android.content.Context
import android.os.Debug
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.RequestQueue.RequestEvent
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

object Http {
    private val serverIP = "http://192.168.43.228:8080"
    private val response = StringBuffer()

    var uuid: String = "empty"
    var result: String? = null

    fun sendCredentials(context: Context, username: String, password: String): RequestQueue {
        val url = "$serverIP/login?username=$username&password=$password"

        val queue = Volley.newRequestQueue(context)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, null,
            {
                result = it.toString()
                Log.d("Http", result.toString())
            },
            {
                print("error")
            }
        )

        queue.add(jsonObjectRequest)

        return queue
    }

    fun getTasks(context: Context, userId: UUID): Request<String> {
        val url = "$serverIP/tasks"
        var resultLength: Int

        val queue = Volley.newRequestQueue(context)

        val stringRequest = StringRequest(Request.Method.POST, url,
            {
                try {
                    val responseObject = JSONObject(it)
                    resultLength = responseObject.length()
                } catch (exc: Throwable) {
                    print(exc.stackTrace)
                }
            },
            {
                Log.d("Http", "Request to '/tasks' failed")
            })

        return queue.add(stringRequest)
    }

    private fun sendGet(path: String, params: String?): String {
        var serverUrl = "$serverIP$path"

        if (params != null) {
            serverUrl += "?${params}"
        }

        with(URL(serverUrl).openConnection() as HttpURLConnection) {
            requestMethod = "GET"

            Log.d("ServerRequest", "URL: $url | Response Code: $responseCode")

            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
            }
            Log.d("Http", "response = $response")
        }

        return simplifyBuffer(response)
    }

    private fun sendPost(path: String, params: String): String {
        val serverUrl = URL("$serverIP$path")

        with (serverUrl.openConnection() as HttpURLConnection) {
            requestMethod = "POST"
            doOutput = true
            setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

            DataOutputStream(outputStream).use { it.writeBytes(params) }

            Log.d("ServerRequest", "URL: $url | Response Code: $responseCode")

            BufferedReader(InputStreamReader(inputStream)).use { bufferedReader ->
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                Log.d("Http", "response = $response")
            }
        }
        return simplifyBuffer(response)
    }

    private fun simplifyBuffer(buffer: StringBuffer): String {
        var result = ""
        for (i in 1 until buffer.capacity() - 1) { result += buffer[i] }
        return result
    }
}