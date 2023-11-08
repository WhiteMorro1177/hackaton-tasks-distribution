package ru.alt.tasksdistribution.requests

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object Http {
    private const val serverIP = "94.139.254.37"
    private val response = StringBuffer()

    fun sendGet(path: String, params: Map<String, String?>?): StringBuffer {
        var serverUrl = "https://$serverIP/$path"
        var reqParams: String? = null

        if (params != null) {
            reqParams = createRequestParameters(params)
            serverUrl += "?${reqParams}"
        }

        with(URL(serverUrl).openConnection() as HttpURLConnection) {
            requestMethod = "POST"

            if (reqParams != null) {
                OutputStreamWriter(outputStream).apply {
                    write(reqParams)
                    flush()
                }
            }

            Log.d("ServerRequest", "URL: $url | Response Code: $responseCode")

            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                println("Response : $response")
            }
        }

        return response
    }

    fun sendPost(path: String, params: Map<String, String?>): StringBuffer {
        val serverUrl = URL("https://$serverIP$path")
        val reqParams: String = createRequestParameters(params)

        with (serverUrl.openConnection() as HttpURLConnection) {
            requestMethod = "POST"

            OutputStreamWriter(outputStream).run {
                write(reqParams)
                flush()
            }

            Log.d("ServerRequest", "URL: $url | Response Code: $responseCode")

            BufferedReader(InputStreamReader(inputStream)).use {
                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                println("Response : $response")
            }
        }
        return response
    }

    private fun String.utf8(): String = URLEncoder.encode(this, "UTF-8")
    private fun createRequestParameters(input: Map<String, String?>): String = input.map { (key, value) -> { "${key.utf8()}=${value.toString().utf8()}" } }.joinToString("&")

}