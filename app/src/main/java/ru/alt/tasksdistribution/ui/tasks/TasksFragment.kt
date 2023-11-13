package ru.alt.tasksdistribution.ui.tasks

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.RequestQueue
import com.google.gson.Gson
import ru.alt.tasksdistribution.databinding.FragmentTasksBinding
import ru.alt.tasksdistribution.helpers.Storage
import ru.alt.tasksdistribution.helpers.TaskConverter
import ru.alt.tasksdistribution.requests.Http
import ru.alt.tasksdistribution.ui.tasks.data.Task
import ru.alt.tasksdistribution.ui.tasks.data.TaskDTO
import ru.alt.tasksdistribution.ui.tasks.data.TasksAdapter

class TasksFragment : Fragment() {
    private val tag = this::class.simpleName


    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(tag, "OnCreateView()")

        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // get list of tasks
        with (Http(requireActivity())) {
            val id = Storage.userId

            Log.d(tag, "Send request from user: id = $id")

            getTasks(id).also {
                it.addRequestEventListener { request, event ->
                    if (event == RequestQueue.RequestEvent.REQUEST_FINISHED) {
                        Log.d(tag, "Extracted response = ${this.jsonResponse}")
                        request.tag = "DONE"

                        // parse response
                        val taskList = arrayListOf<Task>()

                        if (this.jsonResponse == null) {
                            Toast.makeText(this@TasksFragment.context, "Empty response data", Toast.LENGTH_LONG).show()
                        } else {
                            for (index in 0 until this.jsonResponse!!.length()) {
                                Log.d(tag, "Item $index: data: ${this.jsonResponse!![index]} ")

                                taskList.add(
                                    TaskConverter.convert(
                                        Gson().fromJson(jsonResponse!![index].toString(), TaskDTO::class.java)
                                    )
                                )
                            }

                            // fill recycle view
                            binding.taskList.apply {
                                layoutManager = LinearLayoutManager(root.context)
                                adapter = TasksAdapter(taskList, requireActivity())
                            }

                            // save task list
                            Storage.setTaskList(taskList)
                        }
                        it.cancelAll("DONE")
                    }
                }
            }
        }

        Log.d(tag, "End OnCreateView()")
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}