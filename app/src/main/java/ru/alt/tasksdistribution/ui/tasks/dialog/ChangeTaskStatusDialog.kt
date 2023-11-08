package ru.alt.tasksdistribution.ui.tasks.dialog

import android.app.Dialog
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.google.android.material.textfield.TextInputLayout
import ru.alt.tasksdistribution.R
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


    private val statusConverter: Map<TaskStatus, String> = mapOf(
        TaskStatus.ON_WAY to "on_way",
        TaskStatus.IN_PROGRESS to "start",
        TaskStatus.DONE to "completed"
    )

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
        tvDialogTaskTheme.text = holder.tvDestinationToTask.text
        tvStatusValue.text = holder.tvTaskStatus.text

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
            task.timestamps.onWayTimestamp = Date()

            // send request
            // val response = Http.sendPost("/${task.taskAssignmentId}", mapOf("status" to statusConverter[newStatus]))

            dismiss()
        }
        btnInProgress.setOnClickListener {
            val newStatus = TaskStatus.IN_PROGRESS
            tvStatusValue.text = newStatus.name
            holder.tvTaskStatus.text = newStatus.name
            holder.tvTaskStatus.setTextColor(holder.colors["blue"]!!.toInt())
            task.timestamps.startTimestamp = Date()

            // send request
            // val response = Http.sendPost("/${task.taskAssignmentId}", mapOf("status" to statusConverter[newStatus]))

            dismiss()
        }
        btnDone.setOnClickListener {
            val newStatus = TaskStatus.DONE
            tvStatusValue.text = newStatus.name
            holder.tvTaskStatus.text = newStatus.name
            holder.tvTaskStatus.setTextColor(holder.colors["green"]!!.toInt())
            task.timestamps.completionTimestamp = Date()
            val note = when (etTaskComment.text.toString()) {
                "" -> "Task finished"
                else -> etTaskComment.text.toString()
            }

            // send request
            /*val response = Http.sendPost(
                "/${task.taskAssignmentId}",
                mapOf(
                    "status" to statusConverter[newStatus],
                    "note" to note
                )
            )*/

            dismiss()
        }
    }
}