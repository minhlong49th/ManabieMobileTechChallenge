package com.manabie.todo.ui.task

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.snackbar.Snackbar
import com.manabie.todo.R
import com.manabie.todo.data.entities.Task
import com.manabie.todo.data.source.TasksRepository
import com.manabie.todo.databinding.TaskFragmentBinding
import com.manabie.todo.ui.newtask.NewTaskActivity
import com.manabie.todo.utils.*

class TaskFragment : Fragment(), OnClickHandlerInterface {

    private val taskRepository: TasksRepository by lazy {
        ServiceLocator.provideTasksRepository(requireActivity())
    }
    private val taskViewModel by viewModels<TaskViewModel> {
        ViewModelFactory(taskRepository, this)
    }

    private lateinit var viewDataBinding: TaskFragmentBinding
    private lateinit var listAdapter: TasksAdapter

    private val createNewTaskObserver: CreateNewTaskObserver by lazy {
        CreateNewTaskObserver(requireActivity()) {
            taskViewModel.getTasks()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycle.addObserver(createNewTaskObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = TaskFragmentBinding.inflate(inflater, container, false).apply {
            viewmodel = taskViewModel
            hanlder = this@TaskFragment
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
        taskViewModel.item.observe(viewLifecycleOwner, EventObserver { task ->
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
            listAdapter = TasksAdapter(viewModel)
            viewDataBinding.rcyTask.adapter = listAdapter
        }
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(viewLifecycleOwner, taskViewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.fab -> createNewTaskObserver.openScreen()
            else -> {
            }
        }
    }

}

class CreateNewTaskObserver(
    private val activity: FragmentActivity,
    private val resultFunc: ((Intent?) -> Unit)? = null
) : DefaultLifecycleObserver {
    private lateinit var startForResult: ActivityResultLauncher<Intent>

    override fun onCreate(owner: LifecycleOwner) {
        startForResult =
            activity.activityResultRegistry.register(
                "create_task", owner,
                ActivityResultContracts.StartActivityForResult()
            ) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    resultFunc?.invoke(result.data)
                }
            }
    }

    fun openScreen() {
        startForResult.launch(NewTaskActivity.getIntent(activity))
    }
}