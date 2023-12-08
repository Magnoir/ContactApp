package com.example.contactapp

import android.content.Context
import android.os.Bundle
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

class ListContactFragment : Fragment(R.layout.fragment_list_contact) {
    private val contactViewModel: ContactViewModel by activityViewModels()
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inflate the layout for this fragment
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                sharedPreferencesManager.saveLoginStatus(false)
                findNavController().popBackStack()
            }
        })
        sharedPreferencesManager = SharedPreferencesManager(requireContext())

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
        contactViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }
        contactViewModel.newContactIndex.observe(viewLifecycleOwner) { newIndex ->
            newIndex?.let {
                recyclerView.adapter?.notifyItemInserted(newIndex)
            }
        }
        view.findViewById<Button>(R.id.btnGenerateContact).setOnClickListener {
            showLoading(true)
            contactViewModel.fetchContactFromApi()
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

            //Utilisation de Glide pour charger l'image et appliquer le cadre rond
            Glide.with(context)
                .load(currentContact.image) //Image du Contact
                .placeholder(R.drawable.blanc) //fond blanc
                .circleCrop() //cadre rond
                .into(holder.imageViewContactList)

            holder.itemView.setOnClickListener {
                onItemClick(currentContact)
            }
        }
    }

}