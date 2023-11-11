package ru.alt.tasksdistribution.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.alt.tasksdistribution.ui.tasks.data.Task

class MapViewModel : ViewModel() {
    private val _taskList = MutableLiveData<List<Task>>()

    val taskList: LiveData<List<Task>> get() = _taskList

    fun setTaskList(taskList: List<Task>) {
        _taskList.value = emptyList()
        _taskList.value = taskList
    }

}