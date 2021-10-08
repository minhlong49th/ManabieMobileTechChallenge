package com.manabie.todo.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.manabie.todo.data.entities.Task

@Dao
interface TasksDao {
    @Query("SELECT * FROM Tasks WHERE completed = :isCompleted")
    suspend fun getTasks(isCompleted: Boolean): List<Task>

    @Query("SELECT * FROM Tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Query("UPDATE tasks SET completed = :completed WHERE id = :taskId")
    suspend fun updateCompleted(taskId: String, completed: Boolean)
}
