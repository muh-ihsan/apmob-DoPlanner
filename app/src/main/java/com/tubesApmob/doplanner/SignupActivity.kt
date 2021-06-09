package com.tubesApmob.doplanner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.tubesApmob.doplanner.databinding.DplSignupBinding
import timber.log.Timber

class SignupActivity : ActivityIntent() {
    private lateinit var binding: DplSignupBinding
    private lateinit var layoutSignup: ConstraintLayout
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DplSignupBinding.inflate(layoutInflater)
        auth = Firebase.auth
        auth.setLanguageCode("id") // Set bahasa pada firebase menjadi bahasa Indonesia

        layoutSignup = binding.constraintSignup
        val inputNama = binding.inputNamaSignup
        val inputNamaLayout = binding.tiNamaSignup
        val inputEmail = binding.inputEmailSignup
        val inputEmailLayout = binding.tiEmailSignup
        val inputPassword = binding.inputPasswordSignup
        val inputPasswordLayout = binding.tiPasswordSignup
        val inputPasswordUlang = binding.inputPasswordUlangSignup
        val inputPassUlangLayout = binding.tiUlangpassSignup

        binding.btCancelSignup.setOnClickListener {
            val intentLogin = Intent(this, LoginActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intentLogin)
            finish()
        }

        binding.btSignupSignup.setOnClickListener {
            var nama = inputNama.text.toString()
            var email = inputEmail.text.toString().trim()
            var password = inputPassword.text.toString().trim()
            var passwordUlang = inputPasswordUlang.text.toString().trim()

            // Jika salah satu textfield tidak diisi
            if (nama.isEmpty()) {
                inputNamaLayout.error = "Nama tidak boleh kosong"
                return@setOnClickListener
            } else {
                inputNamaLayout.error = null
            }
            if (email.isEmpty()) {
                inputEmailLayout.error = "Nama tidak boleh kosong"
            } else {
                inputEmailLayout.error = null
            }
            if (password.isEmpty()) {
                inputPasswordLayout.error = "Password tidak boleh kosong"
                return@setOnClickListener
            } else {
                inputPasswordLayout.error = null
            }
            if (passwordUlang.isEmpty()) {
                inputPassUlangLayout.error = "Password tidak boleh kosong"
                return@setOnClickListener
            } else {
                inputPassUlangLayout.error = null
            }

            // Jika password tervalidasi
            if (password == passwordUlang) { // Jika password dan password konfirmasi sama
                inputPassUlangLayout.error = null
                signUp(nama, email, password) // Lakukan sign up ke Firebase
            } else { // Jika password dan password konfirmasi beda
                inputPassUlangLayout.error = "Password berbeda"
            }
        }

        setContentView(binding.root)
    }

    private fun signUp(nama: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign up sukses
                    Timber.d("createUserWithEmail:success")
                    val user = auth.currentUser

                    // Menambah data nama user
                    val profileUpdates = userProfileChangeRequest {
                        displayName = nama
                    }
                    user!!.updateProfile(profileUpdates).addOnCompleteListener { taskProfil ->
                        if (taskProfil.isSuccessful) {
                            Timber.d("User profile name added.")
                        } else {
                            Timber.w(taskProfil.exception, "Add user profile name failure")
                        }
                    }

                    // Mengirimkan email verifkasi
                    user!!.sendEmailVerification().addOnCompleteListener { taskEmail ->
                        if (taskEmail.isSuccessful) {
                            Timber.d("Email dikirim.")
                        } else {
                            Timber.w(taskEmail.exception, "Email gagal dikirim.")
                        }
                    }

                    // Menampilkan pesan sukses sign up
                    Snackbar.make(layoutSignup, getString(R.string.dpl_signup_success_description),
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.dpl_login) {
                            keActivityLogin(this); finish()
                        }
                        .show()
                    auth.signOut()

                } else {
                    // Sign up gagal
                    Timber.w(task.exception, "createUserWithEmail:failure")
                    Snackbar.make(layoutSignup, "Sign up gagal: "
                            + task.exception!!.localizedMessage, Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
    }

}