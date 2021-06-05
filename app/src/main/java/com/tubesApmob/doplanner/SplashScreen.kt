package com.tubesApmob.doplanner

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tubesApmob.doplanner.databinding.DplSplashBinding
import timber.log.Timber

class SplashScreen : AppCompatActivity() {
    private lateinit var binding : DplSplashBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DplSplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        val intentMainActivity = Intent(this, MainActivity::class.java)
        val intentLoginActivity = Intent(this, LoginActivity::class.java)

        val userSaatIni = auth.currentUser

        // Method ini untuk splash screen agar menetap lebih lama
        Handler(Looper.getMainLooper()).postDelayed({
            if (userSaatIni != null) { // Jika user sudah login
                startActivity(intentMainActivity) // Mulai menu utama
                finish() // Hentikan activity splash screen
            } else { // Jika user belum login
                startActivity(intentLoginActivity) // Mulai activity login
                finish() // Hentikan activity splash screen
            }
        }, 3000) // delay dalam milliseconds
        Timber.i("Splash screen onCreate dipanggil")
    }
}