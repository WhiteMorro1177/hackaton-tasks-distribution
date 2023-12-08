package ru.alt.tasksdistribution.ui.tasks.dialog

import android.app.Dialog
import android.app.DownloadManager.Request
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.android.volley.RequestQueue
import com.google.android.material.textfield.TextInputLayout
import ru.alt.tasksdistribution.R
import ru.alt.tasksdistribution.requests.Http
import ru.alt.tasksdistribution.ui.tasks.data.Task
import ru.alt.tasksdistribution.ui.tasks.data.TaskColors
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

            // set new values
            val newStatus = TaskStatus.ON_WAY
            tvStatusValue.text = newStatus.name
            holder.tvTaskStatus.text = newStatus.name
            holder.tvTaskStatus.setTextColor(TaskColors.YELLOW.toInt())

            task.timestamps.onWayTimestamp = Date().toString()

            // send request
            with (Http(activity)) {
                setStatus(task.taskId.toString(), uuid, newStatus, "").also {
                    it.addRequestEventListener { _, event ->
                        if (event == RequestQueue.RequestEvent.REQUEST_FINISHED) {
                            Log.d("ChangeTaskStatusDialog", "Status added")
                            this@ChangeTaskStatusDialog.dismiss()
                        }
                    }
                }
            }
        }

        btnInProgress.setOnClickListener {

            // set new values
            val newStatus = TaskStatus.IN_PROGRESS
            tvStatusValue.text = newStatus.name
            holder.tvTaskStatus.text = newStatus.name
            holder.tvTaskStatus.setTextColor(TaskColors.BLUE.toInt())
            task.timestamps.startTimestamp = Date().toString()

            // send request
            with (Http(activity)) {
                setStatus(task.taskId.toString(), uuid, newStatus, "").also {
                    it.addRequestEventListener { _, event ->
                        if (event == RequestQueue.RequestEvent.REQUEST_FINISHED) {
                            Log.d("ChangeTaskStatusDialog", "Status added")
                            this@ChangeTaskStatusDialog.dismiss()
                        }
                    }
                }
            }
        }

        btnDone.setOnClickListener {

            // set new values
            val newStatus = TaskStatus.DONE
            tvStatusValue.text = newStatus.name
            holder.tvTaskStatus.text = newStatus.name
            holder.tvTaskStatus.setTextColor(TaskColors.GREEN.toInt())
            task.timestamps.completionTimestamp = Date().toString()

            // save comment for completed task
            val note = when (etTaskComment.text.toString()) {
                "" -> "Task finished"
                else -> etTaskComment.text.toString()
            }

            with (Http(activity)) {
                setStatus(task.taskId.toString(), uuid, newStatus, note).also {
                    it.addRequestEventListener { _, event ->
                        if (event == RequestQueue.RequestEvent.REQUEST_FINISHED) {
                            Log.d("ChangeTaskStatusDialog", "Status added")
                            this@ChangeTaskStatusDialog.dismiss()
                        }
                    }
                }
            }
        }
    }
}