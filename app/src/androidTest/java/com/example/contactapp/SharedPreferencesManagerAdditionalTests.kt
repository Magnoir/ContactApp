import android.content.Context
import androidx.test.core.app.ApplicationProvider
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
import com.example.contactapp.MainActivity
import org.junit.Rule
import com.example.contactapp.R
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
        sharedPreferencesManager.saveLoginStatus(true)
        val isLoggedIn = sharedPreferencesManager.isLoggedIn()

        assertEquals(true, isLoggedIn)
    }

    @Test
    fun testDefaultLoginStatus() {
        sharedPreferencesManager.saveLoginStatus(false)
        val isLoggedIn = sharedPreferencesManager.isLoggedIn()

        assertEquals(false, isLoggedIn)
    }

    @Test
    fun clickAddContactButton() {
        sharedPreferencesManager.saveLoginStatus(false)

        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()))
        onView(withId(R.id.editTextPassword)).perform(typeText("admin"))
        onView(withId(R.id.btnLogin)).perform(click())

        sharedPreferencesManager = SharedPreferencesManager(ApplicationProvider.getApplicationContext())

        assertEquals(true, sharedPreferencesManager.isLoggedIn())

        onView(withId(R.id.btnGenerateContact)).check(matches(isDisplayed()))
        onView(withId(R.id.btnGenerateContact)).perform(click())

    }
}