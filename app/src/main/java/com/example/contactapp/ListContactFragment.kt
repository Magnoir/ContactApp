package com.example.contactapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ListContactFragment : Fragment() {
    private val contactViewModel: ContactViewModel by activityViewModels()
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferencesManager = SharedPreferencesManager(requireContext())

        val progressBar = view.findViewById<ProgressBar>(R.id.progressBarContact)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewContact)

        contactViewModel.listContacts.observe(viewLifecycleOwner) {
            val sortedContacts = contactViewModel.getSortedContacts()
            val contactAdapter = ContactAdapter(requireContext(), sortedContacts) { selectedContact ->
                // Handle item click here
                contactViewModel.setSelectedContact(selectedContact)
                val action = ListContactFragmentDirections.actionListContactFragmentToShowContactFragment(selectedContact)
                findNavController().navigate(action)
            }
            recyclerView.adapter = contactAdapter
        }
        view.findViewById<Button>(R.id.btnAdd).setOnClickListener {
            findNavController().navigate(R.id.action_listContactFragment_to_addContactFragment)
        }
        view.findViewById<Button>(R.id.btnGenerateContact).setOnClickListener {
            showLoading(true)
            fetchContactFromApi()
        }
    }
    private fun showLoading(isLoading: Boolean) {
        val progressBar = view?.findViewById<ProgressBar>(R.id.progressBarContact)
        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerViewContact)
        val btnGenerateContactButton = view?.findViewById<Button>(R.id.btnGenerateContact)

        if (isLoading) {
            progressBar?.visibility = View.VISIBLE
            recyclerView?.visibility = View.GONE
            btnGenerateContactButton?.isEnabled = false
        } else {
            progressBar?.visibility = View.GONE
            recyclerView?.visibility = View.VISIBLE
            btnGenerateContactButton?.isEnabled = true
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // in here you can do logic when backPress is clicked
                sharedPreferencesManager.saveLoginStatus(false)
                findNavController().popBackStack()
            }
        })

        return inflater.inflate(R.layout.fragment_list_contact, container, false)
    }

    private fun fetchContactFromApi() {
        CoroutineScope(Dispatchers.IO).launch {
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
                        val largeBitmap = contactViewModel.downloadImage(largeImageUrl)
                        // You can do the same for medium and thumbnail sizes if needed

                        val newContact = Contact(
                            user.getJSONObject("name").getString("last"),
                            user.getJSONObject("name").getString("first"),
                            user.getString("phone"),
                            largeBitmap
                        )

                        // Update the ViewModel using the main dispatcher
                        withContext(Dispatchers.Main) {
                            contactViewModel.saveContact(newContact)
                            showLoading(false)
                            Log.d("newContact", newContact.toString())
                            // Notify the adapter that the data set has changed
                            view?.findViewById<RecyclerView>(R.id.recyclerViewContact)?.adapter?.notifyItemInserted(contactViewModel.listContacts.value?.size ?: 0 - 1)
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle any errors
                showLoading(false)
                e.printStackTrace()
            }
        }
    }

    class ContactAdapter(
        private val context: Context,
        private val contacts: List<Contact>,
        private val onItemClick: (Contact) -> Unit
    ) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {


        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textViewName: TextView = view.findViewById(R.id.textViewName)
            val textViewFirstname: TextView = view.findViewById(R.id.textViewFirstname)
            val textViewPhoneNumber: TextView = view.findViewById(R.id.textViewPhoneNumber)
            val imageViewContactList: ImageView = view.findViewById(R.id.imageViewContactList)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.contact_layout_item, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return contacts.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val currentContact = contacts[position]

            holder.textViewName.text = currentContact.name
            holder.textViewFirstname.text = currentContact.firstname
            holder.textViewPhoneNumber.text = currentContact.phoneNumber

            // Utilisation de Glide pour charger l'image et appliquer le cadre rond
            Glide.with(context)
                .load(currentContact.image) // Image du ContactViewModel
                .placeholder(R.drawable.blanc) // Image par défaut ou vide en cas d'échec du chargement
                .circleCrop() // Appliquer le cadre rond
                .into(holder.imageViewContactList)

            // Dans votre ContactAdapter, dans la méthode onBindViewHolder
            holder.itemView.setOnClickListener {
                onItemClick(currentContact)
            }
        }
    }

}