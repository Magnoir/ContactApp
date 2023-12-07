package com.example.contactapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide

class AddContactFragment : Fragment() {

    private val contactViewModel: ContactViewModel by activityViewModels()
    private val addContactViewModel: HoldImageViewModel by activityViewModels()
    private lateinit var imagePicker: ActivityResultLauncher<Intent>
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the ActivityResultLauncher
        imagePicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Handle the result here
                val selectedImageUri = result.data?.data
                addContactViewModel.selectedImageUri = selectedImageUri
                displaySelectedImage(addContactViewModel.selectedImageUri)
            }
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_contact, container, false)
        imageView = view.findViewById(R.id.imageViewAddContact)
        // Inflate the layout for this fragment
        displaySelectedImage(addContactViewModel.selectedImageUri)
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btnLoadImage).setOnClickListener {
            openImagePicker()
        }

        view.findViewById<Button>(R.id.btnAddContact).setOnClickListener {
            val editTextName = view.findViewById<EditText>(R.id.editTextName)
            val textName: String = editTextName.text.toString()
            val editTextFirstname = view.findViewById<EditText>(R.id.editTextFirstName)
            val textFirstname: String = editTextFirstname.text.toString()
            val editTextPhone = view.findViewById<EditText>(R.id.editTextPhone)
            val textPhone: String = editTextPhone.text.toString()

            val imageViewContact = view.findViewById<ImageView>(R.id.imageViewAddContact)
            if (textName.isNotEmpty() && textFirstname.isNotEmpty() && textPhone.isNotEmpty() && addContactViewModel.selectedImageUri != null) {
                // Assuming you have a function to convert ImageView to Bitmap
                val imageBitmap: Bitmap? = imageViewToBitmap(imageViewContact)

                contactViewModel.saveContact(
                    Contact(
                        textName,
                        textFirstname,
                        textPhone,
                        imageBitmap
                    )
                )
                editTextName.setText("")
                editTextFirstname.setText("")
                editTextPhone.setText("")
                imageViewContact.setImageResource(android.R.color.transparent) // Set to a transparent image or your default image
            }
            else {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Incomplete Information")
                builder.setMessage("Please fill in all fields and select an image.")
                builder.setPositiveButton("OK", null)
                val alertDialog = builder.create()
                alertDialog.show()
            }
        }
    }

    // Function to convert ImageView to Bitmap
    private fun imageViewToBitmap(imageView: ImageView): Bitmap? {
        val drawable = imageView.drawable
        val bitmap: Bitmap

        if (drawable is BitmapDrawable) {
            bitmap = drawable.bitmap
        } else {
            // If the drawable is not a BitmapDrawable, create a new Bitmap and draw the drawable on it
            bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
        }
        return bitmap
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePicker.launch(intent)
    }

    private fun displaySelectedImage(imageUri: Uri?) {
        // Display the selected image in the ImageView
        if (imageUri != null) {
            Glide.with(requireContext())
                .load(imageUri)
                .placeholder(R.drawable.blanc)
                .circleCrop()
                .into(imageView)

            // Apply the circular background after loading the image
            imageView.setBackgroundResource(R.drawable.round_border)
        }
    }
}