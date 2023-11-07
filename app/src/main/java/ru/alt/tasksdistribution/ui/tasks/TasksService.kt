package ru.alt.tasksdistribution.ui.tasks

import ru.alt.tasksdistribution.ui.tasks.data.Task
import ru.alt.tasksdistribution.ui.tasks.data.TaskPriority
import ru.alt.tasksdistribution.ui.tasks.data.TaskStatus
import ru.alt.tasksdistribution.ui.tasks.data.TaskTimestamps
import java.util.Date

object TasksService {
    private var tasks = mutableListOf<Task>()

    init {
        tasks = (1..20).map {
            Task(
                taskId = it,
                taskAssignmentId = it,
                priority = TaskPriority(
                    priorityName = "unknown",
                    priorityLevel = it
                ),
                completionTime = 1.5,
                status = TaskStatus.IN_PROGRESS,
                timestamps = TaskTimestamps(
                    assignmentTimestamp = Date(),
                    onWayTimestamp = null,
                    startTimestamp = null,
                    completionTimestamp = null,
                ),
                longitude = 10.0,
                latitude = 10.0
            )
        }.toMutableList()
    }

    fun getTasks() = tasks

}