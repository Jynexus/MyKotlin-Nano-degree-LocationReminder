package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import junit.framework.Assert.assertEquals

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    @Test
    fun onClear_clearsViewModel(){
        // Given a fresh ViewModel
        val saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(),FakeDataSource())

        // When clearing the reminder model
        saveReminderViewModel.onClear()

        // Then all values are null
        var value = saveReminderViewModel.reminderTitle.getOrAwaitValue()
        assertEquals(value,  null )

        value = saveReminderViewModel.reminderDescription.getOrAwaitValue()
        assertEquals(value,  null )

        value = saveReminderViewModel.reminderSelectedLocationStr.getOrAwaitValue()
        assertEquals(value,  null )

        val valuePOI = saveReminderViewModel.selectedPOI.getOrAwaitValue()
        assertEquals(valuePOI,  null )

        var valueDouble = saveReminderViewModel.latitude.getOrAwaitValue()
        assertEquals(valueDouble,  null )

        valueDouble = saveReminderViewModel.longitude.getOrAwaitValue()
        assertEquals(valueDouble,  null )
    }

    @Test
    fun saveReminder_savesReminderToDB() {

        // Given a fresh ViewModel
        val saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(),FakeDataSource())

        // When saving the reminder model
        val reminderDataItem = ReminderDataItem("","","",0.0, 0.0,"Uni")
        saveReminderViewModel.saveReminder(reminderDataItem)

        // Then Values should be

        var loadingValue = saveReminderViewModel.showLoading.getOrAwaitValue()
        assertEquals(loadingValue,  false )

        var toastValue = saveReminderViewModel.showToast.getOrAwaitValue()
        assertEquals(toastValue,  saveReminderViewModel.app.getString(R.string.reminder_saved) )

        var navigationValue = saveReminderViewModel.navigationCommand.getOrAwaitValue()
        assertEquals(navigationValue, NavigationCommand.Back)


    }


}