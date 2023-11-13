package ru.alt.tasksdistribution.requests

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import ru.alt.tasksdistribution.ui.tasks.data.TaskStatus

class Http(private val context: Context) {
    private val tag = this::class.simpleName
    private val serverIP = "http://94.139.254.37:8081"

    var uuid: String = "empty"
    var jsonResponse: JSONArray? = null

    fun sendCredentials(username: String, password: String): RequestQueue {
        return Volley.newRequestQueue(context).apply {
            this.cancelAll("DONE")
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
            this.cancelAll("DONE")
            val url = "$serverIP/tasks?token=$userId"

            JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                {
                    Log.d(tag, "response = $it")
                    jsonResponse = it
                },
                {
                    Log.e(tag, "Error in request - ${it.message}")
                }
            ).also { add(it) }

        }
    }

    fun setStatus(taskId: String, userId: String, newStatus: TaskStatus, note: String): RequestQueue {
        return Volley.newRequestQueue(context).apply {
            this.cancelAll("DONE")
            val url = "$serverIP/task/${taskId}"

            object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    Log.d(tag, "Response = $response")
                },
                Response.ErrorListener { error ->
                    Log.e(tag, "Error in request - Error: ${error.message}")
                }) {

                override fun getParams(): MutableMap<String, String> {
                    return HashMap<String, String>().apply {
                        this["token"] = userId
                        this["status"] = newStatus.name
                        this["note"] = note
                    }
                }
            }.also { this.add(it) }
        }
    }
}