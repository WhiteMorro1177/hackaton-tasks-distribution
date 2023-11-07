package ru.alt.tasksdistribution.ui.tasks.data

import java.util.Date

data class TaskTimestamps (
    val assignmentTimestamp: Date?,
    val onWayTimestamp: Date?,
    val startTimestamp: Date?,
    val completionTimestamp: Date?,
)
