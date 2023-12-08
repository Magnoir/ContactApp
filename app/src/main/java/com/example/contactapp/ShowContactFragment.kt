package com.example.contactapp

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide

class ShowContactFragment : Fragment(R.layout.fragment_show_contact) {
    private lateinit var imageView: ImageView
    private val contactViewModel: ContactViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageView = view.findViewById(R.id.imageViewShowContact)

        // Get the selected contact from the arguments
        val args: ShowContactFragmentArgs by navArgs()
        val selectedContact = args.selectedContact
        Log.d("Contact sélectionné", selectedContact.toString())

        //Updating user interface
        updateUI(selectedContact)
        val btnDeleteContact = view.findViewById<Button>(R.id.btnDelete)
        btnDeleteContact.setOnClickListener {
            contactViewModel.deleteContact(selectedContact)
            findNavController().navigateUp()
        }

        //Get the list of contacts
        val getBackContact = view.findViewById<Button>(R.id.btnGetBack)
        getBackContact.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun updateUI(contact: Contact) {
        // Update name and first name
        val nameTextView = view?.findViewById<TextView>(R.id.textViewShowName)
        nameTextView?.text = contact.name

        val firstNameTextView = view?.findViewById<TextView>(R.id.textViewShowFirstname)
        firstNameTextView?.text = contact.firstname

        // Update phone number
        val phoneNumberTextView = view?.findViewById<TextView>(R.id.textViewShowPhoneNumber)
        phoneNumberTextView?.text = contact.phoneNumber

        //Display the selected image in the ImageView
        displaySelectedImage(contact.image)
    }

    private fun displaySelectedImage(imageBitmap: Bitmap?) {
        if (imageBitmap != null) {
            Glide.with(requireContext())
                .load(imageBitmap)
                .placeholder(R.drawable.blanc)
                .circleCrop()
                .into(imageView)


        }
    }
}
