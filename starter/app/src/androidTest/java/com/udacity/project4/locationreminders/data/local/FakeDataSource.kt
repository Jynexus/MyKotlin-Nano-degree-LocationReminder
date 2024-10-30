package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import org.junit.After
import org.junit.Before

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders : MutableList<ReminderDTO> = mutableListOf()) :
    RemindersDao {

    @Before
    public fun initialize() {
        reminders.clear()
    }

    override suspend fun getReminders(): List<ReminderDTO> {

            return reminders

    }

    override suspend fun getReminderById(reminderId: String): ReminderDTO? {

        return  reminders.filter { it.id == reminderId }.firstOrNull()
    }


    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }

    @After
    public fun clearRemindersList() {
        reminders.clear()
    }

}