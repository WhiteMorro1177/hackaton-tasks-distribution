package ru.alt.tasksdistribution.ui.tasks

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.alt.tasksdistribution.ui.tasks.data.Task
import java.util.UUID

class TasksViewModel : ViewModel() {

    private val _taskList = MutableLiveData<List<Task>>(). apply {
        // get tasks from server

        // val response = Http().sendGet("/tasks/id", mapOf("id" to "<id-value-here>")) // user id
        val data = emptyList<Task>()
        value = TasksService.getTasks() // for now
    }

    private val _userId = MutableLiveData<UUID>()


    val userId: LiveData<UUID> get() = _userId
    val taskList: LiveData<List<Task>> get() = _taskList


    fun setUserId(userId: String) {
        Log.d(this::class.simpleName, userId)
        _userId.value = UUID.fromString(userId)
    }
}