package com.tubesApmob.doplanner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity :  AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dpl_login)

        auth = Firebase.auth // Inisialisasi firebase auth
        val fragmentLogin = LoginFragment() // Deklarasi fragment login

        // Menampilkan fragment login email
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_login_flow, fragmentLogin)
            commit()
        }
    }
}