package com.example.contactapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController

class LoginFragment : Fragment() {
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferencesManager = SharedPreferencesManager(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (sharedPreferencesManager.isLoggedIn()) {
            findNavController().navigate(R.id.action_loginFragment_to_listContactFragment)
        }

        val loginButton: Button = view.findViewById(R.id.btnLogin)
        val editTextPassword = view.findViewById<EditText>(R.id.editTextPassword)

        loginButton.setOnClickListener {
            // Get the entered password
            val enteredPassword: String = editTextPassword.text.toString()

            // Simulate login with a hardcoded password (you should securely handle passwords in a real app)
            val correctPassword = "admin" // Replace with your actual password

            if (enteredPassword == correctPassword) {
                // Password is correct, save login status and navigate to the next fragment
                editTextPassword.setText("")
                sharedPreferencesManager.saveLoginStatus(true)
                Toast.makeText(requireContext(), "Logged in", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_loginFragment_to_listContactFragment)
            } else {
                // Password is incorrect, show a message or handle it accordingly
                Toast.makeText(requireContext(), "Incorrect password", Toast.LENGTH_SHORT).show()
            }
        }

        // Check login status
        Log.d("LoginStatus", "Initial status: ${sharedPreferencesManager.isLoggedIn()}")
    }
}
