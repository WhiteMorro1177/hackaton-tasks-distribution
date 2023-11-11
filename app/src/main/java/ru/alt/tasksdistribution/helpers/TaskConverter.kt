package ru.alt.tasksdistribution.helpers

import ru.alt.tasksdistribution.ui.tasks.data.Task
import ru.alt.tasksdistribution.ui.tasks.data.TaskDTO
import ru.alt.tasksdistribution.ui.tasks.data.TaskStatus
import ru.alt.tasksdistribution.ui.tasks.data.TaskTimestamps
import java.util.Date
import java.util.UUID

object TaskConverter {

    fun convert(toConvert: TaskDTO): Task = Task(
        taskId = toConvert.taskId,
        taskName = toConvert.taskName,
        taskAssignmentId = UUID.fromString(toConvert.taskAssignmentId),
        priorityName = toConvert.priorityName,
        status = TaskStatus.valueOf(toConvert.status),
        timestamps = TaskTimestamps(
            assignmentTimestamp = toConvert.assignmentTimestamp,
            onWayTimestamp = null,
            startTimestamp = null,
            completionTimestamp = null,
        ),
        longitude = toConvert.longitude,
        latitude = toConvert.latitude
    )
}