package ru.alt.tasksdistribution.requests

import android.content.Context
import android.os.Debug
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.RequestQueue.RequestEvent
import com.android.volley.Response
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

class Http(private val context: Context) {
    private val tag = this::class.simpleName
    private val serverIP = "http://94.139.254.37:8081"

    var uuid: String = "empty"
    var jsonResponse: JSONObject? = null

    fun sendCredentials(username: String, password: String): RequestQueue {
        return Volley.newRequestQueue(context).apply {
            this.cancelAll { true }
            val url = "$serverIP/login"

            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    Log.d(tag, "Json object = $response")
                    uuid = response
                    if (uuid.contains("\"")) { uuid = uuid.subSequence(1, uuid.length - 1).toString() }
                },
                Response.ErrorListener { error ->
                    Log.e(tag, "Error in request - ${error.message}")
                }) {

                override fun getParams(): Map<String, String> {
                    return HashMap<String, String>().apply {
                        this["username"] = username
                        this["password"] = password
                    }
                }
            }.also { this.add(it) }
        }
    }

    fun getTasks(userId: String): RequestQueue {
        return Volley.newRequestQueue(context).apply {
            this.cancelAll { true }
            val url = "$serverIP/tasks?token=$userId"

            JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                {
                    Log.d(tag, "respose = $it")
                    jsonResponse = it
                },
                {
                    Log.e(tag, "Error in request - ${it.message}")
                }
            ).also { add(it) }

        }
    }
}