package com.example.contactapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController

class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferencesManager = SharedPreferencesManager(requireContext())
        if (sharedPreferencesManager.isLoggedIn()) {
            findNavController().navigate(R.id.action_loginFragment_to_listContactFragment)
        }

        val loginButton: Button = view.findViewById(R.id.btnLogin)
        val editTextPassword = view.findViewById<EditText>(R.id.editTextPassword)

        loginButton.setOnClickListener {
            //Get the password
            val enteredPassword: String = editTextPassword.text.toString()

            //Simulate login with a hardcoded password
            val correctPassword = "admin"

            if (enteredPassword == correctPassword) {
                //Password is correct so we save login status and navigate to the next fragment
                editTextPassword.setText("")
                sharedPreferencesManager.saveLoginStatus(true)
                Toast.makeText(requireContext(), "Logged in", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_loginFragment_to_listContactFragment)
            } else {
                //Password is incorrect so we show a message
                Toast.makeText(requireContext(), "Incorrect password", Toast.LENGTH_SHORT).show()
            }
        }

        // Check login status
        Log.d("LoginStatus", "Initial status: ${sharedPreferencesManager.isLoggedIn()}")
    }
}
