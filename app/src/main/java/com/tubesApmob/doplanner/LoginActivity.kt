package com.tubesApmob.doplanner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tubesApmob.doplanner.databinding.DplLoginBinding

class LoginActivity :  AppCompatActivity() {
    private lateinit var binding: DplLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DplLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}