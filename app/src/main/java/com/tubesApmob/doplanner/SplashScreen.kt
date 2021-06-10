package com.tubesApmob.doplanner

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tubesApmob.doplanner.databinding.DplSplashBinding
import timber.log.Timber

class SplashScreen : BaseActivity() {
    private lateinit var binding : DplSplashBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DplSplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        val userSaatIni = auth.currentUser

        // Method ini untuk splash screen agar menetap lebih lama
        Handler(Looper.getMainLooper()).postDelayed({
            if (userSaatIni != null) { // Jika user sudah login
                keActivityMain(this)
                finish() // Hentikan activity splash screen
            } else { // Jika user belum login
                keActivityLogin(this) // Mulai activity login
                finish() // Hentikan activity splash screen
            }
        }, 2500) // delay dalam milliseconds
        Timber.i("Splash screen onCreate dipanggil")
    }
}