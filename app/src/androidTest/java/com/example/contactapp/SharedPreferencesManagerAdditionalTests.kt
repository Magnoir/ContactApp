import android.content.Context
import android.util.Log
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import com.example.contactapp.SharedPreferencesManager
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.example.contactapp.MainActivity
import org.junit.Rule
import com.example.contactapp.R
import androidx.test.espresso.Espresso.pressBack
import org.junit.AfterClass
import org.junit.BeforeClass

@RunWith(AndroidJUnit4::class)
class SharedPreferencesManagerTest {
    companion object {
        private lateinit var sharedPreferencesManager: SharedPreferencesManager

        @BeforeClass
        @JvmStatic
        fun setupClass() {
            val context = ApplicationProvider.getApplicationContext<Context>()
            sharedPreferencesManager = SharedPreferencesManager(context)
        }

        @AfterClass
        @JvmStatic
        fun teardownClass() {
            // Clean up any shared resources or dependencies if needed
        }
    }

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        sharedPreferencesManager.saveLoginStatus(false)
    }

    @Test
    fun testSaveLoginStatus() {
        // Save login status as true
        sharedPreferencesManager.saveLoginStatus(true)

        // Retrieve login status
        val isLoggedIn = sharedPreferencesManager.isLoggedIn()

        // Assert that the retrieved login status is true
        assertEquals(true, isLoggedIn)
    }

    @Test
    fun testDefaultLoginStatus() {
        sharedPreferencesManager.saveLoginStatus(false)
        // Retrieve login status without saving it previously
        val isLoggedIn = sharedPreferencesManager.isLoggedIn()

        // Assert that the default login status is false
        assertEquals(false, isLoggedIn)
    }

    @Test
    fun clickAddContactButton() {
        sharedPreferencesManager.saveLoginStatus(false)
        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()))
        // Fill the EditText
        onView(withId(R.id.editTextPassword)).perform(typeText("admin"))
        // Perform UI testing using Espresso
        onView(withId(R.id.btnLogin)).perform(click())
        sharedPreferencesManager = SharedPreferencesManager(ApplicationProvider.getApplicationContext())

        assertEquals(true, sharedPreferencesManager.isLoggedIn())
        // Perform a click on the add contact button
        onView(withId(R.id.btnGenerateContact)).check(matches(isDisplayed()))
        onView(withId(R.id.btnGenerateContact)).perform(click())

    }
}