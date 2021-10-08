package com.manabie.todo.ui.completed

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.manabie.todo.R
import com.manabie.todo.data.entities.Task
import com.manabie.todo.data.source.TasksRepository
import com.manabie.todo.databinding.CompletedTaskFragmentBinding
import com.manabie.todo.utils.EventObserver
import com.manabie.todo.utils.ServiceLocator
import com.manabie.todo.utils.ViewModelFactory
import com.manabie.todo.utils.setupSnackbar

class CompletedTaskFragment : Fragment() {

    private val taskRepository: TasksRepository by lazy {
        ServiceLocator.provideTasksRepository(requireActivity())
    }
    private val completedTaskViewModel by viewModels<CompletedTaskViewModel> {
        ViewModelFactory(taskRepository, this)
    }

    private lateinit var viewDataBinding: CompletedTaskFragmentBinding
    private lateinit var listAdapter: CompletedTasksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = CompletedTaskFragmentBinding.inflate(inflater, container, false).apply {
            viewmodel = completedTaskViewModel
        }

        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        setupSnackbar()
        setupListAdapter()
        setupShowDetailTask()
    }

    private fun setupShowDetailTask() {
        completedTaskViewModel.item.observe(viewLifecycleOwner, EventObserver { task ->
            detailTaskDialog(task)
        })
    }

    private fun detailTaskDialog(task: Task) = AlertDialog.Builder(requireContext())
        .setTitle(task.title)
        .setMessage(task.description)
        .setCancelable(true)
        .setPositiveButton(R.string.btn_ok_alert_dialog) { dialog, _ -> dialog.dismiss() }
        .create()
        .show()

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = CompletedTasksAdapter(viewModel)
            viewDataBinding.rcyCompleted.adapter = listAdapter
        }
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(
            viewLifecycleOwner,
            completedTaskViewModel.snackbarText,
            Snackbar.LENGTH_SHORT
        )
    }

}