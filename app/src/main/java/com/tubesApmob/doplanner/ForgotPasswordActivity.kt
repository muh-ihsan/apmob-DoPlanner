package com.tubesApmob.doplanner

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tubesApmob.doplanner.databinding.DplForgotPasswordBinding
import timber.log.Timber

class ForgotPasswordActivity : BaseActivity() {
    private lateinit var binding: DplForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DplForgotPasswordBinding.inflate(layoutInflater)

        val layoutForgotPass = binding.constraintLupaPass
        val inputEmail = binding.inputEmailLupaPass

        binding.btCancelForgotPass.setOnClickListener {
            val intentLogin = Intent(this, LoginActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intentLogin)
            finish()
        }

        binding.btSubmitForgotPass.setOnClickListener {
            var email = inputEmail.text.toString().trim()

            // Jika email tidak diisi
            if (email.isEmpty()) {
                Snackbar.make(layoutForgotPass, "Email harus diisi", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            Firebase.auth.setLanguageCode("id")
            Firebase.auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.d("Email terkirim.")
                    Snackbar.make(layoutForgotPass, "Email terkirim. Silahkan ikuti " +
                            "langkah-langkah pada email", Snackbar.LENGTH_LONG).show()
                } else {
                    Timber.w(task.exception, "Email gagal terkirim")
                    Snackbar.make(layoutForgotPass, "Gagal: " + task.exception!!.localizedMessage, Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        setContentView(binding.root)
    }
}