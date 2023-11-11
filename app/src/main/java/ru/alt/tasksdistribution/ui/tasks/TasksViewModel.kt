package ru.alt.tasksdistribution.ui.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.alt.tasksdistribution.ui.tasks.data.Task
import java.util.UUID

class TasksViewModel : ViewModel() {
    private val _userId = MutableLiveData<UUID>()
    private val _taskList = MutableLiveData<List<Task>>()

    val userId: LiveData<UUID> get() = _userId
    val taskList: LiveData<List<Task>> get() = _taskList

    fun setUserId(userId: String) {
        _userId.value = UUID.fromString(userId)
    }
    fun setTaskList(taskList: List<Task>) {
        _taskList.value = taskList
    }
}