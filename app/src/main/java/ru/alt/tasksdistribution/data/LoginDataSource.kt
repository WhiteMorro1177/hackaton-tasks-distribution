package ru.alt.tasksdistribution.data

import android.util.Log
import ru.alt.tasksdistribution.data.model.LoggedInUser
import java.io.IOException
import java.util.UUID


class LoginDataSource {

    fun login(login: String, password: String): Result<LoggedInUser> {
        return try {
            val serverPath = "/login"

            val response: StringBuffer = ru.alt.tasksdistribution.requests.Http.sendPost(serverPath, mapOf("username" to login, "password" to password))

            Log.d("LoginActivity", response.toString())

            val userId = UUID.randomUUID()
            val fakeUser = LoggedInUser(userId, login, "User${userId.toString().subSequence(0, 5)}")
            Result.Success(fakeUser)
        } catch (e: Throwable) {
            Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
    }
}