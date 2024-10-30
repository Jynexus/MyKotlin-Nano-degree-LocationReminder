package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun loadReminders_checkValuesWithEmptyList(){

        // Given a fresh ViewModel
        val remindersListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            FakeDataSource()
        )

        mainCoroutineRule.pauseDispatcher()
        // When loading the reminders
        remindersListViewModel.loadReminders()

        var value = remindersListViewModel.showLoading.getOrAwaitValue()
        assertEquals(value,true)

        mainCoroutineRule.resumeDispatcher()
        //Then
        value  = remindersListViewModel.showNoData.getOrAwaitValue()
        assertEquals(value,true)


        var reminders = remindersListViewModel.remindersList.getOrAwaitValue()
        assertEquals(reminders.count(),0)

    }

    @Test
    fun loadTasks_loading() {
        // Given a fresh ViewModel
        val remindersListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            FakeDataSource()
        )

        // Pause dispatcher so you can verify initial values.
        mainCoroutineRule.pauseDispatcher()

        // Load the task in the view model.
        remindersListViewModel.loadReminders()

        // Then assert that the progress indicator is shown.
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(true))

        // Execute pending coroutines actions.
        mainCoroutineRule.resumeDispatcher()

        // Then assert that the progress indicator is hidden.
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun loadReminders_showLoading() {
        val remindersListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            FakeDataSource()
        )
        mainCoroutineRule.pauseDispatcher()

        remindersListViewModel.loadReminders()

        assertEquals(remindersListViewModel.showLoading.getOrAwaitValue(),true)

        mainCoroutineRule.resumeDispatcher()

        assertEquals(remindersListViewModel.showLoading.getOrAwaitValue(),false)
    }

    @Test
    fun loadReminders_updateSnackBarValue() {
        val repo = FakeDataSource()
        repo.shouldReturnError = true
        val remindersListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            repo
        )
        mainCoroutineRule.pauseDispatcher()

        remindersListViewModel.loadReminders()

        mainCoroutineRule.resumeDispatcher()

        Truth.assertThat(remindersListViewModel.showSnackBar.getOrAwaitValue()).isEqualTo("Error loading.")
    }

    @After
    fun reset() {
        stopKoin()
    }

}