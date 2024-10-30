package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

class FakeTestRepo(var reminders : MutableList<ReminderDTO>? = mutableListOf()) : ReminderDataSource {

    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) {
            return Result.Error("Test Error")
        }
        return try {
            Result.Success(data = reminders!!)
        } catch (e : Exception){
            Result.Error(e.message)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders!!.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        return try {
            var reminder = reminders!!.filter { it.id == id }.first()
            Result.Success(reminder)
        } catch (e : Exception){
            Result.Error(e.message)
        }
    }

    override suspend fun deleteAllReminders() {
        reminders!!.clear()
    }
}