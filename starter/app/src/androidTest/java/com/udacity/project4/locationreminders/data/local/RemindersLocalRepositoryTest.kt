package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.nullValue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    //var mainCoroutineRule = MainCoroutineRule() --> using this Coroutine Caused Job didn't finish Exception

    lateinit var LocalDataSource : RemindersDao
    lateinit var remindersRepo : RemindersLocalRepository
    lateinit var  reminderDTO : ReminderDTO
    lateinit var database :RemindersDatabase

    @Before
    fun createRepository() {
        reminderDTO = ReminderDTO("Test Repo", "Repo Description","Repo Test Location",0.0,0.0,"-1")
        database =  Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java)
            .allowMainThreadQueries() // Without Cannot access database on the main thread Exception
            .build()
        LocalDataSource = database.reminderDao()
        // Get a reference to the class under test.
        remindersRepo = RemindersLocalRepository(
            // HERE Swap Dispatcher.Unconfined
            LocalDataSource, Dispatchers.Main
        )
    }



//    TODO: Add testing implementation to the RemindersLocalRepository.kt
    @Test
    fun getReminders_confrimsThatListhasElement() = runBlocking {
        remindersRepo.saveReminder(reminderDTO)
        var result = remindersRepo.getReminders()
        assertEquals(result is Result.Success,true)
    }

    @Test
    fun getRemindersById_valid()= runBlocking{
        remindersRepo.saveReminder(reminderDTO)
        var result = remindersRepo.getReminder("-1")
        assertEquals(result is Result.Success,true)
    }

    @Test
    fun getRemindersById_invalid()= runBlocking{
        remindersRepo.saveReminder(reminderDTO)
        var result = remindersRepo.getReminder("-9") as com.udacity.project4.locationreminders.data.dto.Result.Error
        assertEquals(result is Result.Error,true)
        assertEquals("Reminder not found!",result.message)
    }

    @Test
    fun saveReminder()= runBlocking {
        var reminderNewDTO = ReminderDTO("Test Repo new", "Repo Description new","Repo Test Location new",0.0,0.0,"-2")
        remindersRepo.saveReminder(reminderNewDTO)
        assertEquals(LocalDataSource.getReminderById("-2")!!.title,"Test Repo new")
    }

    @Test
    fun deleteAllReminders()= runBlocking {
        remindersRepo.deleteAllReminders()
        assertEquals(LocalDataSource.getReminders()!!.count(), 0)
    }

    @After
    fun cleanUp() {
        database.close()
    }

}
