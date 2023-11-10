package ru.alt.tasksdistribution.ui.tasks

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import java.util.UUID

class TasksViewModel : ViewModel() {
    private val _userId = MutableLiveData<UUID>()
    private val _taskList = MutableLiveData<String>()
    private val _recyclerView = MutableLiveData<RecyclerView>()

    val userId: LiveData<UUID> get() = _userId
    val taskList: LiveData<String> get() = _taskList
    val recyclerView: LiveData<RecyclerView> get() = _recyclerView

    fun setUserId(userId: String) {
        Log.d(this::class.simpleName, userId)
        _userId.value = UUID.fromString(userId)
    }
    fun setTaskList(taskList: String) {
        Log.d(this::class.simpleName, taskList)
        _taskList.value = taskList
    }
    fun setRecyclerView(recyclerView: RecyclerView) {
        _recyclerView.value = recyclerView
    }
}