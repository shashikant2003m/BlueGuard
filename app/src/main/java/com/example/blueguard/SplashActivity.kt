package com.example.blueguard

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null) {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        } else {
            // If user is NOT logged in, go to WelcomeActivity
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
        }

        // Finish this activity so the user can't navigate back to it
        finish()
    }
}
