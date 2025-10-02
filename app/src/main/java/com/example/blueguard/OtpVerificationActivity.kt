package com.example.blueguard

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class OtpVerificationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var otpFields: List<EditText>
    private lateinit var verifyButton: Button
    private var verificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)

        auth = FirebaseAuth.getInstance()
        verificationId = intent.getStringExtra("verificationId")

        otpFields = listOf(
            findViewById(R.id.otpDigit1),
            findViewById(R.id.otpDigit2),
            findViewById(R.id.otpDigit3),
            findViewById(R.id.otpDigit4),
            findViewById(R.id.otpDigit5),
            findViewById(R.id.otpDigit6)
        )

        setupOtpInputs()

        verifyButton = findViewById(R.id.verifyOtpButton)
        verifyButton.setOnClickListener {
            val otp = otpFields.joinToString("") { it.text.toString().trim() }
            if (otp.length == 6 && verificationId != null) {
                val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
                signInWithPhoneAuthCredential(credential)
            } else {
                Toast.makeText(this, "Enter valid OTP", Toast.LENGTH_SHORT).show()
            }
        }

        // Auto show keyboard on first digit
        otpFields[0].requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(otpFields[0], InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setupOtpInputs() {
        for (i in otpFields.indices) {
            otpFields[i].filters = arrayOf(InputFilter.LengthFilter(1))

            otpFields[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!s.isNullOrEmpty()) {
                        if (i < otpFields.size - 1) {
                            otpFields[i + 1].requestFocus()
                        }
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })

            otpFields[i].setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                    if (otpFields[i].text.isEmpty() && i > 0) {
                        otpFields[i - 1].apply {
                            requestFocus()
                            setSelection(text.length)
                        }
                    }
                }
                false
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "OTP Verification Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
