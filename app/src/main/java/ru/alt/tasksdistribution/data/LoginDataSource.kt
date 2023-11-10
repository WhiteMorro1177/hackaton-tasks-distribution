package ru.alt.tasksdistribution.data

import android.content.Context
import android.util.Log
import com.android.volley.RequestQueue.RequestEvent
import ru.alt.tasksdistribution.MainActivity
import ru.alt.tasksdistribution.data.model.LoggedInUser
import ru.alt.tasksdistribution.requests.Http
import ru.alt.tasksdistribution.ui.login.LoginActivity
import java.io.IOException
import java.net.URL
import java.util.UUID
import kotlin.concurrent.thread


class LoginDataSource {

    fun login(context: Context, username: String, password: String): Result<LoggedInUser> {
        return try {
            // send login request
            Http.sendCredentials(context, "anton", "anton").run {
                addRequestEventListener { request, event ->
                    if (event == RequestEvent.REQUEST_FINISHED) {
                        val result = request.body.toString()
                        Log.d("LoginDataSource", "request finished - result body = $result")

                        val userId = UUID.fromString(result)

                        val fakeUser = LoggedInUser(userId!!, username, "User${userId.toString().subSequence(0, 5)}")
                        Log.d("LoginActivity", "Called Result.Success()")
                        Result.Success(fakeUser)
                    }
                }
            }


            val userId: UUID? = null
            val fakeUser = LoggedInUser(userId!!, username, "User${userId.toString().subSequence(0, 5)}")
            Log.d("LoginActivity", "Called Result.Success()")
            Result.Success(fakeUser)
        } catch (e: Throwable) {
            Log.d("LoginActivity", "Called Result.Error() - ${e.message}")
            Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
    }
}