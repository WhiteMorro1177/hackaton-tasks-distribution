package ru.alt.tasksdistribution.ui.tasks.data

import java.util.UUID

data class TaskDTO(
    val empId: Number,
    val note: String,
    val taskName: String,
    val taskId: Number,
    val taskAssignmentId: String,
    val priorityName: String,
    val status: String,
    val assignmentTimestamp: String,
    val onWayTimestamp: String?,
    val startTimestamp: String?,
    val completionTimestamp: String?,
    val longitude: Double,
    val latitude: Double,
)