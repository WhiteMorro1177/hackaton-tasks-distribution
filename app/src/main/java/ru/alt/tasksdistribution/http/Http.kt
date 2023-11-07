package ru.alt.tasksdistribution.http

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class Http {
    private val serverIP = "94.139.254.37"
    private val response = StringBuffer()

    fun sendGet(path: String, params: List<String>?): StringBuffer {
        var serverUrl = "https://$serverIP/$path"
        var reqParams: String? = null

        if (params != null) {
            reqParams = createRequestParameters(params)
            serverUrl += "?${reqParams}"
        }

        with(URL(serverUrl).openConnection() as HttpURLConnection) {
            // optional default is GET
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

    fun sendPost(path: String, params: List<String>): StringBuffer {
        val serverUrl = URL("https://$serverIP/$path")
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
            }
        }
        return response
    }

    private fun createRequestParameters(input: List<String>): String {
        var result: String = input[0]
        if (input.size > 1) {
            for (i in 2..input.size) {
                result += "&${input[i]}"
            }
        }
        return result
    }
}