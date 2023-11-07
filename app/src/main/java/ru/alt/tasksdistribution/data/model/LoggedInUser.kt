package ru.alt.tasksdistribution.data.model

import java.util.UUID


data class LoggedInUser(
    val userId: UUID,
    val email: String,
    val displayName: String
)