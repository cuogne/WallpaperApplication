package com.cuogne.wallpaperapplication.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cuogne.wallpaperapplication.R
import com.cuogne.wallpaperapplication.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.ClearCredentialException
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager
    private lateinit var btnLogout: ImageButton
    private lateinit var btnBackInProfile: ImageButton
    private lateinit var helloUser: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)

        btnLogout = findViewById(R.id.btnLogout)
        btnBackInProfile = findViewById(R.id.btnBackInProfile)
        helloUser = findViewById(R.id.helloUser)

        val currentUser = auth.currentUser
        helloUser.text = "Hello, ${currentUser?.displayName}"

        btnLogout.setOnClickListener {
            logout()
        }

        btnBackInProfile.setOnClickListener {
            finish()
        }
    }

    private fun logout() {
        auth.signOut()

        lifecycleScope.launch {
            try {
                credentialManager.clearCredentialState(
                    ClearCredentialStateRequest()
                )
            } catch (e: ClearCredentialException) {
                Log.e("ProfileActivity", "Clear credential failed", e)
            } finally {
                finish()
            }
        }
    }
}
