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
        val tvTaskStatus = binding.tvTaskStatus
        val layoutListItem = binding.layoutListItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTasksBinding.inflate(inflater, parent, false)

        return TasksViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val task = tasks[position]

        holder.tvTaskTheme.text = task.taskName
        holder.tvTaskStatus.text = task.status.name
        holder.tvTaskStatus.setTextColor(task.statusColor.toInt())
        holder.layoutListItem.setOnClickListener {
            // open dialog
            ChangeTaskStatusDialog(activity, holder, task).show()
        }
    }

    override fun getItemCount(): Int = tasks.size
}