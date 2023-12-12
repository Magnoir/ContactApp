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
        val contact1 = Contact("Doe", "John", "1234567890", null)
        val contact2 = Contact("Smith", "Jane", "0987654321", null)
        //we verify that the two contacts are not equal
        assertNotEquals(contact1, contact2)
    }
    @Test
    fun contactEqualsTest() {
        val contact1 = Contact("Doe", "John", "1234567890", null)
        val contact2 = Contact("Doe", "John", "1234567890", null)
        //We verify that the two contacts are equal
        assertEquals(contact1, contact2)
    }
    @Test
    fun contactPropertiesTest() {
        val contact = Contact("Doe", "John", "1234567890", null)
        //we verify that the properties are set correctly
        assertEquals("Doe", contact.name)
        assertEquals("John", contact.firstname)
        assertEquals("1234567890", contact.phoneNumber)
        assertEquals(null, contact.image)
    }
    @Test
    fun contactParcelableNotEqualTest() {
        val originalContact = Contact("Doe", "John", "1234567890", null)
        val differentContact = Contact("Smith", "Jane", "0987654321", null)
        val parcel = mock(Parcel::class.java)
        //We write the data of the original contact
        originalContact.writeToParcel(parcel, originalContact.describeContents())
        //We reset the parcel for reading
        parcel.setDataPosition(0)
        //Reading the data
        val createdFromParcel = Contact.createFromParcel(parcel)
        //We check that the received data is not equal to the different contact
        assertNotEquals(differentContact, createdFromParcel)
    }
    @Test
    fun contactDescribeContentsTest() {
        val contact = Contact("Doe", "John", "1234567890", null)
        //We verify the method result
        assertEquals(0, contact.describeContents())
    }

}