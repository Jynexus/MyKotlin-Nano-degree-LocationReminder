package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.local.RemindersDao
import org.junit.After
import org.junit.Before

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders : MutableList<ReminderDTO> = mutableListOf()) : ReminderDataSource  {

    var shouldReturnError  = false

    @Before public fun initialize() {
    reminders.clear()
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if(shouldReturnError == true) {
            return Result.Error("Error loading.")
        }
        return try {
            Result.Success(data = reminders)
        } catch (e : Exception){
            Result.Error(e.message)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if(!shouldReturnError) {
            try {
                var reminder = reminders.filter { it.id == id }.first()
                return Result.Success(reminder)
            }
            catch (e : Exception){ return Result.Error("Reminder not found!") }
        }
        else{
            return Result.Error("Reminder not found!")
        }
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }

    @After  fun clearRemindersList() {
        reminders.clear()
    }

}