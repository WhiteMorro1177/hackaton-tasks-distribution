package ru.alt.tasksdistribution.ui.tasks.data

import java.util.UUID

data class Task(
    val taskId: Number,
    val taskName: String,
    val taskAssignmentId: UUID,
    val priorityName: String,
    val status: TaskStatus, // enum
    val timestamps: TaskTimestamps, // data class
    val longitude: Double,
    val latitude: Double,
)
