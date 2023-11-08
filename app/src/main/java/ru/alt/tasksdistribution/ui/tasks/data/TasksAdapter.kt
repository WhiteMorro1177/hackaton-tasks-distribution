package ru.alt.tasksdistribution.ui.tasks.data

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import ru.alt.tasksdistribution.databinding.ItemTasksBinding
import ru.alt.tasksdistribution.ui.tasks.dialog.ChangeTaskStatusDialog

class TasksAdapter(val tasks: List<Task>, private val activity: FragmentActivity) : RecyclerView.Adapter<TasksAdapter.TasksViewHolder>() {

    class TasksViewHolder(binding: ItemTasksBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvTaskTheme = binding.tvTaskTheme
        val tvTaskAddress = binding.tvTaskAddress // maybe get from MapFragment
        val tvTaskStatus = binding.tvTaskStatus
        val tvDestinationToTask = binding.tvDestinationToTask
        val layoutListItem = binding.layoutListItem

        val colors = mapOf(
            "red" to 0xffff0000,
            "green" to 0xff00ff00,
            "blue" to 0xff00ffff,
            "yellow" to 0xffffff00
        )
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
        holder.tvTaskStatus.setTextColor(holder.colors["red"]!!.toInt())
        holder.layoutListItem.setOnClickListener {
            // open dialog
            ChangeTaskStatusDialog(activity, holder, task).show()
        }
    }

    override fun getItemCount(): Int = tasks.size
}