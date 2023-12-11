package com.example.contactapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class ContactViewModelAdditionalTests {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: ContactViewModel

    @Before
    fun setup() {
        viewModel = ContactViewModel()
    }

    @Test
    fun saveContactTest() = runBlockingTest {
        val contact = Contact("Doe", "John", "1234567890", null)
        viewModel.saveContact(contact)

        val observer = mock(Observer::class.java) as Observer<MutableList<Contact>>
        viewModel.listContacts.observeForever(observer)

        assertEquals(1, viewModel.listContacts.value?.size)
        assertEquals(contact, viewModel.listContacts.value?.get(0))
    }

    @Test
    fun deleteContactTest() = runBlockingTest {
        val contact = Contact("Doe", "John", "1234567890", null)
        viewModel.saveContact(contact)
        viewModel.deleteContact(contact)

        val observer = mock(Observer::class.java) as Observer<MutableList<Contact>>
        viewModel.listContacts.observeForever(observer)

        assertEquals(0, viewModel.listContacts.value?.size)
    }
    @Test
    fun fetchContactFromApiTest() = runBlockingTest {
        // Mock the Contact
        val contact = mock(Contact::class.java)

        // Call the method
        viewModel.fetchContactFromApi()

        // Verify that the contact was added to the list
        val observer = mock(Observer::class.java) as Observer<MutableList<Contact>>
        viewModel.listContacts.observeForever(observer)

        // Since we can't predict the exact contact that will be fetched from the API,
        // we can at least check that a contact was added to the list
        assertEquals(1, viewModel.listContacts.value?.size)
    }
}