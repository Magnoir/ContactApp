package com.example.contactapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ContactViewModel : ViewModel() {

    private val _listContacts: MutableLiveData<MutableList<Contact>> = MutableLiveData()
    val listContacts: LiveData<MutableList<Contact>> get() = _listContacts

    private val _selectedContact: MutableLiveData<Contact?> = MutableLiveData()
    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _newContactIndex: MutableLiveData<Int> = MutableLiveData()
    val newContactIndex: LiveData<Int> get() = _newContactIndex

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

    private fun downloadImage(imageUrl: String): Bitmap? {
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

    fun fetchContactFromApi() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Make the API call for one contact
                val url = URL("https://randomuser.me/api/?inc=name,phone,picture&nat=fr&results=1")
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "GET"
                connection.connect()

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                    val response = StringBuilder()

                    var line: String?
                    while (bufferedReader.readLine().also { line = it } != null) {
                        response.append(line)
                    }

                    bufferedReader.close()

                    // Parse the JSON response
                    val jsonObject = JSONObject(response.toString())
                    val results = jsonObject.getJSONArray("results")
                    if (results.length() > 0) {
                        val user = results.getJSONObject(0)

                        // Extracting image URLs
                        val largeImageUrl = user.getJSONObject("picture").getString("large")

                        // Downloading the image
                        val largeBitmap = downloadImage(largeImageUrl)
                        // You can do the same for medium and thumbnail sizes if needed

                        val newContact = Contact(
                            user.getJSONObject("name").getString("last"),
                            user.getJSONObject("name").getString("first"),
                            user.getString("phone"),
                            largeBitmap
                        )
                        Log.d("Log",newContact.toString())
                        // Update the ViewModel using the main dispatcher
                        withContext(Dispatchers.Main) {
                            saveContact(newContact)
                            _isLoading.value = false
                            Log.d("newContact", newContact.toString())
                            // Notify the adapter that the data set has changed
                            _newContactIndex.value = (listContacts.value?.size ?: 0) - 1
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle any errors
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                    e.printStackTrace()
                }
            }
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