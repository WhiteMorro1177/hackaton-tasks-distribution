package ru.alt.tasksdistribution.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ru.alt.tasksdistribution.databinding.FragmentTasksBinding
import ru.alt.tasksdistribution.ui.tasks.data.TasksAdapter

class TasksFragment : Fragment() {

    private var _binding: FragmentTasksBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val tasksViewModel: TasksViewModel = ViewModelProvider(this)[TasksViewModel::class.java]

        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        val root: View = binding.root

        tasksViewModel.taskList.observe(viewLifecycleOwner) {
            val recyclerView = binding.taskList
            recyclerView.layoutManager = LinearLayoutManager(root.context)
            recyclerView.adapter = TasksAdapter(it, requireActivity())
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}