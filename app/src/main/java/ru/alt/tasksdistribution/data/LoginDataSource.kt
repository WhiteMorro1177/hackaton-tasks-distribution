package ru.alt.tasksdistribution.data

import ru.alt.tasksdistribution.data.model.LoggedInUser
import ru.alt.tasksdistribution.http.Http
import java.io.IOException
import java.util.UUID


class LoginDataSource {

    fun login(login: String, password: String): Result<LoggedInUser> {
        return try {
            val serverPath = "login"

            // val response: StringBuffer = Http().sendPost(serverPath, listOf("login=$login", "password=$password"))

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