package ru.alt.tasksdistribution.ui.tasks

import android.util.Log
import ru.alt.tasksdistribution.requests.Http
import ru.alt.tasksdistribution.ui.tasks.data.Task
import ru.alt.tasksdistribution.ui.tasks.data.TaskPriority
import ru.alt.tasksdistribution.ui.tasks.data.TaskStatus
import ru.alt.tasksdistribution.ui.tasks.data.TaskTimestamps
import java.util.Date

class TasksService(val id: String) {
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
                status = TaskStatus.ASSIGNED,
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

        // val response = Http.execute("GET", "/tasks", "token=${id}").get()
        // Log.d("TasksService", "response = $response")
    }

    fun getTasks() = tasks

}