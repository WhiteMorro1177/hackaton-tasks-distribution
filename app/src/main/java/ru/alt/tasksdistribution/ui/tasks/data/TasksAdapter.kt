package ru.alt.tasksdistribution.ui.tasks.data

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.alt.tasksdistribution.databinding.ItemTasksBinding

class TasksAdapter(val tasks: List<Task>) : RecyclerView.Adapter<TasksAdapter.TasksViewHolder>() {

    class TasksViewHolder(binding: ItemTasksBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvTaskTheme = binding.tvTaskTheme
        val tvTaskAddress = binding.tvTaskAddress
        val tvTaskStatus = binding.tvTaskStatus
        val tvDestinationToTask = binding.tvDestinationToTask
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTasksBinding.inflate(inflater, parent, false)

        return TasksViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val task = tasks[position]

        holder.tvTaskTheme.text = "Task No.${task.taskId}"
        holder.tvTaskStatus.text = task.status.name
    }

    override fun getItemCount(): Int = tasks.size
}