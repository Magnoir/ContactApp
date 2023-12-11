package com.example.contactapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

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
    /*
    @Test
    fun saveAndDeleteContactTest() = runBlockingTest {
        // Create a Contact and save it
        val contact = Contact("Doe", "John", "1234567890", null)
        viewModel.saveContact(contact)

        // Verify that the Contact was saved
        val observer = mock(Observer::class.java) as Observer<MutableList<Contact>>
        viewModel.listContacts.observeForever(observer)

        assertEquals(1, viewModel.listContacts.value?.size)
        assertEquals(contact, viewModel.listContacts.value?.get(0))

        // Delete the Contact
        viewModel.deleteContact(contact)

        // Verify that the Contact was deleted
        assertEquals(0, viewModel.listContacts.value?.size)
    }
     */
    @Test
    fun getSortedContactsTest() = runBlockingTest {
        // Create and save multiple Contacts
        val contact1 = Contact("Doe", "John", "1234567890", null)
        val contact2 = Contact("Smith", "Jane", "0987654321", null)
        val contact3 = Contact("Brown", "Bob", "1122334455", null)

        viewModel.saveContact(contact1)
        viewModel.saveContact(contact2)
        viewModel.saveContact(contact3)

        // Get the sorted list of Contacts
        val sortedContacts = viewModel.getSortedContacts()

        // Verify that the Contacts are sorted by firstname
        val sortedByFirstname = sortedContacts.sortedBy { it.firstname }
        assertEquals(sortedByFirstname, sortedContacts)
    }
}
