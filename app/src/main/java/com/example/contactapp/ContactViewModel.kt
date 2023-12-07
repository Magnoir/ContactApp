package com.example.contactapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class ContactViewModel : ViewModel() {

    private val _listContacts: MutableLiveData<MutableList<Contact>> = MutableLiveData()
    val listContacts: LiveData<MutableList<Contact>> get() = _listContacts

    private val _selectedContact: MutableLiveData<Contact?> = MutableLiveData()

    init {
        _listContacts.value = mutableListOf()
        _selectedContact.value = null
    }

    fun saveContact(contact: Contact) {
        val currentList = _listContacts.value ?: mutableListOf()
        currentList.add(contact)
        _listContacts.value = currentList
    }

    fun deleteContact(contact: Contact) {
        val currentList = _listContacts.value ?: mutableListOf()
        currentList.remove(contact)
        _listContacts.value = currentList
    }

    fun setSelectedContact(contact: Contact?) {
        _selectedContact.value = contact
    }

    fun getSortedContacts(): List<Contact> {
        return _listContacts.value?.sortedBy { it.firstname } ?: emptyList()
    }

    fun downloadImage(imageUrl: String): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}


data class Contact(
    val name: String,
    val firstname: String,
    val phoneNumber: String,
    val image: Bitmap?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readParcelable(Bitmap::class.java.classLoader, Bitmap::class.java)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(firstname)
        parcel.writeString(phoneNumber)
        parcel.writeParcelable(image, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Contact> {
        override fun createFromParcel(parcel: Parcel): Contact {
            return Contact(parcel)
        }

        override fun newArray(size: Int): Array<Contact?> {
            return arrayOfNulls(size)
        }
    }
}