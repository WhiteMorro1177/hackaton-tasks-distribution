package ru.alt.tasksdistribution.ui.tasks.data

data class Task(
    val taskId: Number,
    val taskAssignmentId: Number,
    val priority: TaskPriority, // create data class
    val completionTime: Double,
    val status: TaskStatus, // create enum
    val timestamps: TaskTimestamps, // create data class
    val longitude: Double,
    val latitude: Double,
)
