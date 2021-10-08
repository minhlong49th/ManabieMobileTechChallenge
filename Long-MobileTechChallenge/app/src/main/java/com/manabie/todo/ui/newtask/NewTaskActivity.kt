package com.manabie.todo.ui.newtask

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.manabie.todo.R
import com.manabie.todo.data.source.TasksRepository
import com.manabie.todo.databinding.ActivityNewTaskBinding
import com.manabie.todo.utils.EventObserver
import com.manabie.todo.utils.ServiceLocator
import com.manabie.todo.utils.ViewModelFactory
import com.manabie.todo.utils.setupSnackbar

class NewTaskActivity : AppCompatActivity() {

    private lateinit var viewDataBinding: ActivityNewTaskBinding

    private val taskRepository: TasksRepository by lazy {
        ServiceLocator.provideTasksRepository(this)
    }

    private val viewModel by viewModels<NewTaskViewModel> { ViewModelFactory(taskRepository, this) }

    companion object {
        fun getIntent(activity: Activity) = Intent(activity, NewTaskActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupActionBar()

        viewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_task)
        viewDataBinding.viewmodel = viewModel

        setupSnackbar()
        setupSavedEvent()
    }

    private fun setupActionBar() {
        title = getString(R.string.add_new_task)
        actionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun setupSnackbar() {
        viewDataBinding.root.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }

    private fun setupSavedEvent() {
        viewModel.taskSavedEvent.observe(this, EventObserver {
            setResult(RESULT_OK)
            finish()
        })
    }
}