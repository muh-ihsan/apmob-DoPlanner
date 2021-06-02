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

        /* val fragmentLogin = LoginFragment() // Deklarasi fragment login

        // Menampilkan fragment login email
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_login_flow, fragmentLogin)
            commit()
        } */
    }

    public override fun onStart() {
        super.onStart()

    }

    // fungsi untuk memulai activity login
    fun keActivityLogin() {
        val intentLogin = Intent(this, LoginActivity::class.java)
        startActivity(intentLogin)
    }
}