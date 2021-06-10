package com.tubesApmob.doplanner

import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tubesApmob.doplanner.databinding.DplLoginBinding
import timber.log.Timber

class LoginActivity :  BaseActivity() {
    private lateinit var binding: DplLoginBinding
    private lateinit var layoutLogin: ConstraintLayout
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DplLoginBinding.inflate(layoutInflater)
        auth = Firebase.auth
        db = Firebase.firestore

        layoutLogin = binding.constraintLogin
        val inputEmail = binding.inputEmailLogin
        val inputPassword = binding.inputPasswordLogin

        binding.btLoginLogin.setOnClickListener {
            var email = inputEmail.text.toString().trim()
            var password = inputPassword.text.toString().trim()

            // Jika satu atau lebih textfield tidak diisi
            if (email.isEmpty()) {
                Snackbar.make(layoutLogin, "Email tidak boleh kosong", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                Snackbar.make(layoutLogin, "Password tidak boleh kosong", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            signIn(email, password)
        }

        binding.btSignupLogin.setOnClickListener {
            keActivitySignup(this)
        }

        binding.btForgotpassLogin.setOnClickListener {
            keActivityLupaPass(this)
        }

        setContentView(binding.root)
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in berhasil
                    Timber.d("signInWithEmail:success")
                    val user = auth.currentUser
                    if (user!!.isEmailVerified) {
                        Timber.d("emailVerified:success")
                        firstSetup()
                    } else {
                        Timber.w(task.exception, "emailVerified:failure")
                        Snackbar.make(layoutLogin, "Email belum diverifikasi",
                            Snackbar.LENGTH_INDEFINITE).setAction("Kirim Email") {
                                user.sendEmailVerification().addOnCompleteListener { taskEmail ->
                                    if (taskEmail.isSuccessful) {
                                        Timber.d("Email dikirim.")
                                    } else {
                                        Timber.w(task.exception, "Email gagal dikirim.")
                                    }
                                }
                            }
                            .show()
                        auth.signOut()
                    }
                } else {
                    // Sign in gagal
                    Timber.w(task.exception, "signInWithEmail:failure")
                    Snackbar.make(layoutLogin, "Login gagal: " + task.exception!!.localizedMessage,
                        Snackbar.LENGTH_SHORT).show()
                }
            }
    }

    // Jika akun pertama kali dibuat, maka akan diarahkan ke first setup
    private fun firstSetup() {
        val uid = auth.currentUser!!.uid
        val userRef = db.collection("users").document(uid)
        userRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Timber.d("Profil sudah ada di database")
                    keActivityMain(this)
                    finish()
                } else {
                    Timber.d("Profil belum dibuat di database")
                    keActivityFirstSetup(this)
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Timber.w(e, "gagal dengan ")
            }
    }
}