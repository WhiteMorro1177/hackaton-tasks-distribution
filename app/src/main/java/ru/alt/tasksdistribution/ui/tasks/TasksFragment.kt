package ru.alt.tasksdistribution.ui.tasks

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.RequestQueue
import com.google.gson.Gson
import ru.alt.tasksdistribution.databinding.FragmentTasksBinding
import ru.alt.tasksdistribution.helpers.TaskConverter
import ru.alt.tasksdistribution.requests.Http
import ru.alt.tasksdistribution.ui.map.MapViewModel
import ru.alt.tasksdistribution.ui.tasks.data.Task
import ru.alt.tasksdistribution.ui.tasks.data.TaskDTO
import ru.alt.tasksdistribution.ui.tasks.data.TasksAdapter
import java.util.UUID

class TasksFragment : Fragment() {
    private val tag = this::class.simpleName

    private var _binding: FragmentTasksBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(tag, "OnCreateView()")
        val tasksViewModel: TasksViewModel = ViewModelProvider(requireActivity())[TasksViewModel::class.java]
        val mapViewModel: MapViewModel = ViewModelProvider(requireActivity())[MapViewModel::class.java]

        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        val root: View = binding.root

        var userId: UUID? = null
        binding.btnRefresh.setOnClickListener {
            try {
                tasksViewModel.userId.observe(viewLifecycleOwner) { uuid ->
                    userId = uuid
                }

                with (Http(requireContext())) {
                    getTasks(userId.toString()).also {
                        it.addRequestEventListener { request, event ->
                            if (event == RequestQueue.RequestEvent.REQUEST_FINISHED) {
                                Log.d(tag, "Extracted response = ${this.jsonResponse}")
                                request.tag = "DONE"

                                // parse response
                                val taskList = arrayListOf<Task>()

                                for (index in 0 until this.jsonResponse!!.length()) {
                                    Log.d(tag, "Item $index: data: ${this.jsonResponse!![index]} ")

                                    taskList.add(
                                        TaskConverter.convert(
                                            Gson().fromJson(jsonResponse!![index].toString(), TaskDTO::class.java)
                                        )
                                    )
                                }

                                binding.taskList.apply {
                                    layoutManager = LinearLayoutManager(root.context)
                                    adapter = TasksAdapter(taskList, requireActivity())
                                    binding.btnRefresh.visibility = View.GONE
                                    visibility = View.VISIBLE
                                }

                                Log.d(tag, "Task list = $taskList")

                                tasksViewModel.setTaskList(taskList)
                                mapViewModel.setTaskList(taskList)

                                it.cancelAll("DONE")
                            }
                        }
                    }
                }
            } catch (exc: Throwable) {
                Log.e(tag, exc.message!!)
                Toast.makeText(context, "Wait until tasks downloaded", Toast.LENGTH_LONG).show()
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