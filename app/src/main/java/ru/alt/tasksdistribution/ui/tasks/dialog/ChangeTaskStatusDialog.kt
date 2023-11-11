package ru.alt.tasksdistribution.ui.tasks.dialog

import android.app.Dialog
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import ru.alt.tasksdistribution.R
import ru.alt.tasksdistribution.requests.Http
import ru.alt.tasksdistribution.ui.map.MapViewModel
import ru.alt.tasksdistribution.ui.tasks.TasksViewModel
import ru.alt.tasksdistribution.ui.tasks.data.Task
import ru.alt.tasksdistribution.ui.tasks.data.TaskStatus
import ru.alt.tasksdistribution.ui.tasks.data.TasksAdapter
import java.util.Date

class ChangeTaskStatusDialog(
    activity: FragmentActivity,
    holder: TasksAdapter.TasksViewHolder,
    task: Task
): Dialog(activity) {

    private var btnOnWay: Button
    private var btnInProgress: Button
    private var btnDone: Button

    private var tvDialogTaskTheme: TextView
    private var tvStatusValue: TextView

    private var etBase: TextInputLayout
    private var etTaskComment: EditText

    init {
        setContentView(R.layout.fragment_change_task_status_dialog)

        // bind components
        btnOnWay = findViewById(R.id.btnDialogOnWay)
        btnInProgress = findViewById(R.id.btnDialogInProgress)
        btnDone = findViewById(R.id.btnDialogDone)
        tvDialogTaskTheme = findViewById(R.id.tvDialogTaskTheme)
        tvStatusValue = findViewById(R.id.tvDialogStatusValue)
        etBase = findViewById(R.id.etDialogBase)
        etTaskComment = findViewById(R.id.etDialogTaskComment)

        // set text
        tvDialogTaskTheme.text = holder.tvTaskTheme.text
        tvStatusValue.text = holder.tvTaskStatus.text

        val taskViewModel = ViewModelProvider(activity)[TasksViewModel::class.java]
        val mapViewModel = ViewModelProvider(activity)[MapViewModel::class.java]

        // enable buttons
        when (tvStatusValue.text) {
            TaskStatus.ASSIGNED.name -> btnOnWay.isEnabled = true
            TaskStatus.ON_WAY.name -> btnInProgress.isEnabled = true
            TaskStatus.IN_PROGRESS.name ->  {
                btnDone.isEnabled = true
                etBase.isEnabled = true
            }
        }

        // set click event handlers
        btnOnWay.setOnClickListener {
            val newStatus = TaskStatus.ON_WAY
            tvStatusValue.text = newStatus.name
            holder.tvTaskStatus.text = newStatus.name
            holder.tvTaskStatus.setTextColor(holder.colors["yellow"]!!.toInt())

            task.timestamps.onWayTimestamp = Date().toString()

            val tl = arrayListOf<Task>()
            val mainTl = arrayListOf<Task>()
            taskViewModel.taskList.observe(activity) {
                mainTl.addAll(it)
            }

            for (t in mainTl) {
                if (task.taskAssignmentId == t.taskAssignmentId) {
                    tl.add(Task(
                        t.taskId,
                        t.taskName,
                        t.taskAssignmentId,
                        t.priorityName,
                        TaskStatus.ON_WAY,
                        task.timestamps,
                        t.longitude,
                        t.latitude
                    ))
                } else {
                    tl.add(t)
                }
            }

            taskViewModel.setTaskList(tl)
            mapViewModel.setTaskList(tl)

            // send request
            with (Http(activity)) {
                setStatus(task.taskId.toString(), uuid, newStatus, "")
            }

            dismiss()
        }

        btnInProgress.setOnClickListener {
            val newStatus = TaskStatus.IN_PROGRESS
            tvStatusValue.text = newStatus.name
            holder.tvTaskStatus.text = newStatus.name
            holder.tvTaskStatus.setTextColor(holder.colors["blue"]!!.toInt())
            task.timestamps.startTimestamp = Date().toString()


            val tl = arrayListOf<Task>()
            val mainTl = arrayListOf<Task>()
            taskViewModel.taskList.observe(activity) {
                mainTl.addAll(it)
            }

            for (t in mainTl) {
                if (task.taskAssignmentId == t.taskAssignmentId) {
                    tl.add(Task(
                        t.taskId,
                        t.taskName,
                        t.taskAssignmentId,
                        t.priorityName,
                        TaskStatus.IN_PROGRESS,
                        task.timestamps,
                        t.longitude,
                        t.latitude
                    ))
                } else {
                    tl.add(t)
                }
            }

            taskViewModel.setTaskList(tl)
            mapViewModel.setTaskList(tl)

            // send request
            with (Http(activity)) {
                setStatus(task.taskId.toString(), uuid, newStatus, "")
            }

            dismiss()
        }
        btnDone.setOnClickListener {
            val newStatus = TaskStatus.DONE
            tvStatusValue.text = newStatus.name
            holder.tvTaskStatus.text = newStatus.name
            holder.tvTaskStatus.setTextColor(holder.colors["green"]!!.toInt())
            task.timestamps.completionTimestamp = Date().toString()
            val note = when (etTaskComment.text.toString()) {
                "" -> "Task finished"
                else -> etTaskComment.text.toString()
            }


            val tl = arrayListOf<Task>()
            val mainTl = arrayListOf<Task>()
            taskViewModel.taskList.observe(activity) {
                mainTl.addAll(it)
            }

            for (t in mainTl) {
                if (task.taskAssignmentId == t.taskAssignmentId) {
                    tl.add(Task(
                        t.taskId,
                        t.taskName,
                        t.taskAssignmentId,
                        t.priorityName,
                        TaskStatus.DONE,
                        task.timestamps,
                        t.longitude,
                        t.latitude
                    ))
                } else {
                    tl.add(t)
                }
            }

            taskViewModel.setTaskList(tl)
            mapViewModel.setTaskList(tl)

            with (Http(activity)) {
                setStatus(task.taskId.toString(), uuid, newStatus, note)
            }

            dismiss()
        }
    }
}