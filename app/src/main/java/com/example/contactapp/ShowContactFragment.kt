package com.example.contactapp

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        // Récupérer les arguments avec les données du contact

        val args: ShowContactFragmentArgs by navArgs()
        val selectedContact = args.selectedContact
        Log.d("Contact sélectionné", selectedContact.toString())

        // Utiliser les données pour mettre à jour l'interface utilisateur
        updateUI(selectedContact)
        val btnDeleteContact = view.findViewById<Button>(R.id.btnDelete)
        btnDeleteContact.setOnClickListener {
            contactViewModel.deleteContact(selectedContact)
            findNavController().navigateUp()
        }

        val getBackContact = view.findViewById<Button>(R.id.btnGetBack)
        getBackContact.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun updateUI(contact: Contact) {
        // Mettez à jour l'interface utilisateur avec les détails du contact (nom, prénom, numéro de téléphone, image, etc.)

        // Update name and first name
        val nameTextView = view?.findViewById<TextView>(R.id.textViewShowName)
        nameTextView?.text = contact.name

        val firstNameTextView = view?.findViewById<TextView>(R.id.textViewShowFirstname)
        firstNameTextView?.text = contact.firstname

        // Update phone number
        val phoneNumberTextView = view?.findViewById<TextView>(R.id.textViewShowPhoneNumber)
        phoneNumberTextView?.text = contact.phoneNumber

        // Display the selected image in the ImageView
        displaySelectedImage(contact.image)
    }

    private fun displaySelectedImage(imageBitmap: Bitmap?) {
        // Display the selected image in the ImageView
        if (imageBitmap != null) {
            Glide.with(requireContext())
                .load(imageBitmap)
                .placeholder(R.drawable.blanc)
                .circleCrop()
                .into(imageView)


        }
    }
}
