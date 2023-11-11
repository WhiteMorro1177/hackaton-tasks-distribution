package ru.alt.tasksdistribution.ui.tasks.data

import java.util.Date

data class TaskTimestamps(
    val assignmentTimestamp: String,
    var onWayTimestamp: String?,
    var startTimestamp: String?,
    var completionTimestamp: String?,
)
