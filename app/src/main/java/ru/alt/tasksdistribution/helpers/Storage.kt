package ru.alt.tasksdistribution.helpers

import ru.alt.tasksdistribution.ui.tasks.data.Task
import java.util.UUID

object Storage {
    private lateinit var _userId: String
    private lateinit var _taskList: List<Task>

    val userId get() = _userId
    val taskList get() = _taskList

    fun setId(uuid: String) {
        _userId = uuid
    }
    fun setTaskList(tl: List<Task>) {
        _taskList = tl
    }
}