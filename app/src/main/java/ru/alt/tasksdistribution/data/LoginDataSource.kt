package ru.alt.tasksdistribution.data

import android.util.Log
import ru.alt.tasksdistribution.data.model.LoggedInUser
import ru.alt.tasksdistribution.requests.Http
import java.io.IOException
import java.util.UUID


class LoginDataSource {

    fun login(username: String, password: String): Result<LoggedInUser> {
        return try {
            val stringUUID: String = Http.execute( "POST", "/login", "username=$username&password=$password").get()

            Log.d("LoginActivity", "response = $stringUUID")

            val userId = UUID.fromString(stringUUID)
            val fakeUser = LoggedInUser(userId, username, "User${userId.toString().subSequence(0, 5)}")
            Log.d("LoginActivity", "Called Result.Success()")
            Result.Success(fakeUser)
        } catch (e: Throwable) {
            Log.d("LoginActivity", "Called Result.Error()")
            Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
    }
}