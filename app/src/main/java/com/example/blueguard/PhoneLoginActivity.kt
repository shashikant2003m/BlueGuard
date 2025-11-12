package com.example.blueguard

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class PhoneLoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var nameInput: EditText
    private lateinit var phoneNumberInput: EditText
    private lateinit var continueButton: TextView
    private lateinit var sharedPreferences: SharedPreferences

    private var verificationId: String? = null
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    companion object {
        private const val TAG = "PhoneLoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_login)

        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        nameInput = findViewById(R.id.nameInput)
        phoneNumberInput = findViewById(R.id.phoneNumberInput)
        continueButton = findViewById(R.id.continueButton)

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)

                when {
                    e.message?.contains("BILLING_NOT_ENABLED") == true -> {
                        showError("Billing not enabled. Please upgrade your Firebase project to Blaze plan.")
                        Toast.makeText(this@PhoneLoginActivity,
                            "Firebase project needs billing enabled for SMS verification",
                            Toast.LENGTH_LONG).show()
                    }
                    e.message?.contains("QUOTA_EXCEEDED") == true -> {
                        showError("SMS quota exceeded. Please try again later.")
                    }
                    e.message?.contains("INVALID_PHONE_NUMBER") == true -> {
                        showError("Invalid phone number format.")
                    }
                    else -> {
                        showError("Verification Failed: ${e.message}")
                    }
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "onCodeSent:$verificationId")
                super.onCodeSent(verificationId, token)

                this@PhoneLoginActivity.verificationId = verificationId

                val userName = nameInput.text.toString().trim()
                saveUserName(userName)

                Toast.makeText(this@PhoneLoginActivity, "OTP sent successfully!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this@PhoneLoginActivity, OtpVerificationActivity::class.java)
                intent.putExtra("verificationId", verificationId)
                intent.putExtra("userName", userName)
                startActivity(intent)
            }
        }

        continueButton.setOnClickListener {
            validateAndProceed()
        }
    }

    private fun validateAndProceed() {
        val name = nameInput.text.toString().trim()
        val phoneNumber = phoneNumberInput.text.toString().trim()

        // Validate inputs
        if (name.isEmpty()) {
            showError("Please enter your name")
            nameInput.requestFocus()
            return
        }

        if (name.length < 2) {
            showError("Name must be at least 2 characters")
            nameInput.requestFocus()
            return
        }

        if (phoneNumber.length != 10) {
            showError("Please enter a valid 10-digit phone number")
            phoneNumberInput.requestFocus()
            return
        }

        hideError()

        val fullPhoneNumber = "+91$phoneNumber"
        Log.d(TAG, "Starting verification for: $fullPhoneNumber")
        startPhoneVerification(fullPhoneNumber)
    }

    private fun startPhoneVerification(phoneNumber: String) {
        try {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)

            Toast.makeText(this, "Sending verification code...", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Log.e(TAG, "Error starting phone verification", e)
            showError("Failed to start verification: ${e.message}")
        }
    }

    private fun saveUserName(name: String) {
        val editor = sharedPreferences.edit()
        editor.putString("user_name", name)
        editor.apply()
        Log.d(TAG, "User name saved: $name")
    }

    private fun showError(message: String) {
        val errorText: TextView = findViewById(R.id.errorText)
        errorText.text = message
        errorText.visibility = android.view.View.VISIBLE
    }

    private fun hideError() {
        val errorText: TextView = findViewById(R.id.errorText)
        errorText.visibility = android.view.View.GONE
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    showError("Verification Failed: ${task.exception?.message}")
                }
            }
    }
}