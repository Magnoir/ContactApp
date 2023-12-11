package com.example.contactapp
import androidx.lifecycle.MutableLiveData
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock
import org.mockito.Mockito
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.delay

@RunWith(JUnit4::class)
class ContactViewModelTest {

    private lateinit var viewModel: ContactViewModel

    @Before
    fun setup() {
        viewModel = mock(ContactViewModel::class.java)
        Mockito.`when`(viewModel.listContacts).thenReturn(MutableLiveData())
    }

    @Test
    fun fetchContactFromApiTest() = runBlockingTest {
        // Call the function to test
        viewModel.fetchContactFromApi()

        // Delay to wait for the result
        delay(1000)

        // Check the result
        assertNotNull(viewModel.listContacts.value)
    }
}