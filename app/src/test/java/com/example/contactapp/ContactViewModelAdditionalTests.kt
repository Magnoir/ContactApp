package com.example.contactapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock


@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
class ContactViewModelAdditionalTests {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: ContactViewModel
    @Before
    fun setup() {
        viewModel = ContactViewModel()
    }

    @Test
    fun saveContactTest() = runTest {
        val contact = Contact("Doe", "John", "1234567890", null)
        viewModel.saveContact(contact)

        val observer = mock(Observer::class.java) as Observer<MutableList<Contact>>
        viewModel.listContacts.observeForever(observer)

        assertEquals(1, viewModel.listContacts.value?.size)
        assertEquals(contact, viewModel.listContacts.value?.get(0))
    }

    @Test
    fun deleteContactTest() = runTest {
        val contact = Contact("Doe", "John", "1234567890", null)
        viewModel.saveContact(contact)
        viewModel.deleteContact(contact)

        val observer = mock(Observer::class.java) as Observer<MutableList<Contact>>
        viewModel.listContacts.observeForever(observer)

        assertEquals(0, viewModel.listContacts.value?.size)
    }
    @Test
    fun getSortedContactsTest() = runTest {
        val contact1 = Contact("Doe", "John", "1234567890", null)
        val contact2 = Contact("Smith", "Jane", "0987654321", null)
        val contact3 = Contact("Brown", "Bob", "1122334455", null)

        viewModel.saveContact(contact1)
        viewModel.saveContact(contact2)
        viewModel.saveContact(contact3)

        //We get the sorted list of Contacts
        val sortedContacts = viewModel.getSortedContacts()

        //and verify that the Contacts are sorted by firstname
        val sortedByFirstname = sortedContacts.sortedBy { it.firstname }
        assertEquals(sortedByFirstname, sortedContacts)
    }
    @Test
    fun fetchContactFromApiTest() = runTest {
        viewModel.fetchContactFromApi()

        //we add a delay to wait for the result
        delay(2000)

        Assert.assertNotNull(viewModel.listContacts.value)
    }
}
