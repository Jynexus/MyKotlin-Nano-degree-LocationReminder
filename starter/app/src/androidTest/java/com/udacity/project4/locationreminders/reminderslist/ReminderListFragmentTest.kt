package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.content.Context
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.testing.withFragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.android.material.internal.ContextUtils.getActivity
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.FakeTestRepo
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest : AutoCloseKoinTest(){

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application

    @Before
    fun initRepository() {
        stopKoin()
        /**
         * use Koin Library as a service locator
         */
        appContext = getApplicationContext()
        val myModule = module {
            //Declare a ViewModel - be later inject into Fragment with dedicated injector using by viewModel()
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            //Declare singleton definitions to be later injected using by inject()
            single {
                //This view model is declared singleton to be used across multiple fragments
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { FakeTestRepo() as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }

        startKoin {
            modules(listOf(myModule))
        }

        repository = get()
    }

    @Test
    fun clickAddReminderFAB_navigateToSaveReminder() {

        (repository as FakeTestRepo).setReturnError(false)
        // GIVEN - On the home screen
        val scenario = launchFragmentInContainer<ReminderListFragment>(null, R.style.AppTheme)

        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // WHEN - Click on the first list item
        onView(withId(R.id.addReminderFAB))
            .perform(click())


        // THEN - Verify that we navigate to the first detail screen
        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder( )
        )

        Thread.sleep(9000)
    }

    @Test
    fun reminders_DisplayedInUi() {
        (repository as FakeTestRepo).setReturnError(false)
        runTest {
            // GIVEN - Add reminder to the DB
            val reminder = ReminderDTO("zero Location", "I don't know Where", "Unknown", 0.0, 0.0, "-1")
            repository.saveReminder(reminder)

            // WHEN - remindersList fragment launched to display reminder
            launchFragmentInContainer<ReminderListFragment>(null, R.style.AppTheme)

            // THEN - Task details are displayed on the screen
            // make sure that the title/description/location are both shown and correct
            onView(withId(R.id.title)).check(matches(isDisplayed()))
            onView(withId(R.id.title)).check(matches(withText("zero Location")))
            onView(withId(R.id.description)).check(matches(isDisplayed()))
            onView(withId(R.id.description)).check(matches(withText("I don't know Where")))
            onView(withId(R.id.location)).check(matches(isDisplayed()))
            onView(withId(R.id.location)).check(matches(withText("Unknown")))

            Thread.sleep(2000)


        }

    }
//    TODO: add testing for the error messages.
    @Test
    fun error_DisplayedInMsg(){
    // GIVEN - Error load reminders
    (repository as FakeTestRepo).setReturnError(true)

    // WHEN - remindersList fragment launched to display reminder
    launchFragmentInContainer<ReminderListFragment>(null, R.style.AppTheme)

    // THEN - Error Message is shown
    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText("Test Error")))


    Thread.sleep(9000)
    }

    @After
    fun cleanup()  {
        (repository as FakeTestRepo).setReturnError(false)
        stopKoin()
        val myModule = module {
            //Declare a ViewModel - be later inject into Fragment with dedicated injector using by viewModel()
            viewModel {
                RemindersListViewModel(
                    get(),
                    get() as ReminderDataSource
                )
            }
            //Declare singleton definitions to be later injected using by inject()
            single {
                //This view model is declared singleton to be used across multiple fragments
                SaveReminderViewModel(
                    get(),
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(getApplicationContext()) }
        }

        startKoin {
            androidContext(getApplicationContext())
            modules(listOf(myModule))
        }
    }
}