package ru.alt.tasksdistribution.ui.login

import java.util.UUID


data class LoggedInUserView(
    val userId: UUID,
    val displayName: String,
    val email: String,
)