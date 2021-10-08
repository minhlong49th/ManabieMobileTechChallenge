package com.manabie.todo.utils

import androidx.lifecycle.LiveData
import org.junit.Assert

fun assertSnackbarMessage(snackbarLiveData: LiveData<Event<Int>>, messageId: Int) {
    val value: Event<Int> = snackbarLiveData.getOrAwaitValue()
    Assert.assertEquals(value.getContentIfNotHandled(), messageId)
}

fun assertLiveDataEventTriggered(
    liveData: LiveData<Event<String>>,
    taskId: String
) {
    val value = liveData.getOrAwaitValue()
    Assert.assertEquals(value.getContentIfNotHandled(), taskId)
}