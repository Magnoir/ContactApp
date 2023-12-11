package com.example.contactapp

import android.graphics.Bitmap
import android.os.Parcel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock

@RunWith(JUnit4::class)
class ContactAdditionalTests {

    @Test
    fun contactNotEqualTest() {
        // Create two different Contact objects
        val contact1 = Contact("Doe", "John", "1234567890", null)
        val contact2 = Contact("Smith", "Jane", "0987654321", null)

        // Verify that the two objects are not equal
        assertNotEquals(contact1, contact2)
    }
    @Test
    fun contactEqualsTest() {
        // Create two identical Contact objects
        val contact1 = Contact("Doe", "John", "1234567890", null)
        val contact2 = Contact("Doe", "John", "1234567890", null)

        // Verify that the two objects are equal
        assertEquals(contact1, contact2)
    }
    @Test
    fun contactPropertiesTest() {
        // Create a Contact object
        val contact = Contact("Doe", "John", "1234567890", null)

        // Verify that the properties are set correctly
        assertEquals("Doe", contact.name)
        assertEquals("John", contact.firstname)
        assertEquals("1234567890", contact.phoneNumber)
        assertEquals(null, contact.image)
    }
    @Test
    fun contactParcelableNotEqualTest() {
        // Create two different Contact objects
        val originalContact = Contact("Doe", "John", "1234567890", null)
        val differentContact = Contact("Smith", "Jane", "0987654321", null)

        // Mock the Parcel
        val parcel = mock(Parcel::class.java)

        // Write the data of the original contact
        originalContact.writeToParcel(parcel, originalContact.describeContents())

        // After you're done with writing, you need to reset the parcel for reading
        parcel.setDataPosition(0)

        // Read the data
        val createdFromParcel = Contact.createFromParcel(parcel)

        // Verify that the received data is not equal to the different contact
        assertNotEquals(differentContact, createdFromParcel)
    }
    @Test
    fun contactDescribeContentsTest() {
        // Create a Contact object
        val contact = Contact("Doe", "John", "1234567890", null)

        // Call the method and verify the result
        assertEquals(0, contact.describeContents())
    }

}