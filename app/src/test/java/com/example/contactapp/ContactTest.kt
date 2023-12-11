package com.example.contactapp

import android.graphics.Bitmap
import android.os.Parcel
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(JUnit4::class)
class ContactTest {

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
    fun contactParcelableTest() {
        // Create a Contact object
        val originalContact = Contact("Doe", "John", "1234567890", null)

        // Mock the Parcel
        val parcel = mock(Parcel::class.java)

        // Write the data
        originalContact.writeToParcel(parcel, originalContact.describeContents())

        // After you're done with writing, you need to reset the parcel for reading
        parcel.setDataPosition(0)

        // Read the data
        val createdFromParcel = Contact.createFromParcel(parcel)

        // Verify that the received data is correct
        assertEquals(originalContact.name, createdFromParcel.name)
        assertEquals(originalContact.firstname, createdFromParcel.firstname)
        assertEquals(originalContact.phoneNumber, createdFromParcel.phoneNumber)
        assertEquals(originalContact.image, createdFromParcel.image)
    }
}