package ru.alt.tasksdistribution.ui.tasks.data

import java.util.Date

data class TaskTimestamps (
    val assignmentTimestamp: Date?,
    var onWayTimestamp: Date?,
    var startTimestamp: Date?,
    var completionTimestamp: Date?,
)
