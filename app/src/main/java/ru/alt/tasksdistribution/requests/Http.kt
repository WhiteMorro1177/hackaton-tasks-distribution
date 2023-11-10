package ru.alt.tasksdistribution.requests

import android.os.AsyncTask
import android.util.Log
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object Http : AsyncTask<String, String, String>() {
    private const val serverIP = "http://192.168.1.59:8080"
    private val response = StringBuffer()

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

    override fun doInBackground(vararg params: String?): String {
        val reqType = params[0]!!
        val reqPath = params[1]!!
        val reqParams = params[2]

        if (reqType == "GET") {
            return sendGet(reqPath, reqParams)
        }
        if (reqType == "POST") {
            return sendPost(reqPath, reqParams!!)
        }
        return ""
    }
}