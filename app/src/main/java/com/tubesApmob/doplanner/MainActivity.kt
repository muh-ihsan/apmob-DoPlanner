package com.tubesApmob.doplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initiate Firebase auth
        auth = Firebase.auth

        // Cek apakah user sudah sign in (non-null) dan masuk ke menu utama jika signed in
        val userSaatIni = auth.currentUser
        if (userSaatIni != null) {
            setContentView(R.layout.activity_main)
        } else {
            keActivityLogin()
        }

    }


    // fungsi untuk memulai activity login
    private fun keActivityLogin() {
        val intentLogin = Intent(this, LoginActivity::class.java)
        startActivity(intentLogin)
    }
}