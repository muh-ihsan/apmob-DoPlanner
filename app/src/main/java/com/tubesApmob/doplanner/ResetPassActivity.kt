package com.tubesApmob.doplanner

import android.os.Bundle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tubesApmob.doplanner.databinding.DplResetPasswordBinding
import timber.log.Timber

class ResetPassActivity : BaseActivity() {
    private lateinit var binding: DplResetPasswordBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DplResetPasswordBinding.inflate(layoutInflater)
        auth = Firebase.auth
        db = Firebase.firestore
        val user = auth.currentUser
        val inputPasswordBaru = binding.inputPasswordReset
        val inputPasswordUlang = binding.inputPasswordUlangReset
        val layoutReset = binding.constraintResetPass

        binding.topAppBarResetpass.setOnClickListener {
            finish()
        }

        binding.btSubmitReset.setOnClickListener {
            val passwordBaru = inputPasswordBaru.text.toString()
            val passwordUlang = inputPasswordUlang.text.toString()

            if (passwordBaru.isEmpty()) {
                binding.tiPasswordReset.error = "Wajib diisi"
                return@setOnClickListener
            } else {
                binding.tiPasswordReset.error = null
            }
            if (passwordUlang.isEmpty()) {
                binding.tiPasswordUlangReset.error = "Wajib diisi"
                return@setOnClickListener
            } else {
                binding.tiPasswordUlangReset.error = null
            }

            if (passwordBaru == passwordUlang) {
                binding.tiPasswordUlangReset.error = null

                // Menampilkan alert re-autentikasi
                val builder = MaterialAlertDialogBuilder(this)
                val inflater = layoutInflater
                val dialogLayout = inflater.inflate(R.layout.dpl_alert_reautentikasi, null)
                val inputPassword =
                    dialogLayout.findViewById<TextInputEditText>(R.id.input_password_alert)
                builder.setTitle("Masukkan Password Saat Ini")
                builder.setView(dialogLayout)
                builder.setPositiveButton("Submit") { dialog, i ->
                    val password = inputPassword.text.toString()
                    val credential = EmailAuthProvider.getCredential(user!!.email!!, password)

                    // Re-autentifikasi user
                    user.reauthenticate(credential).addOnSuccessListener { // Jika autentifikasi sukses
                        Timber.d("Sukses re-autentikasi user")

                        // Mengupdate password ke firebase
                        user.updatePassword(passwordBaru).addOnSuccessListener {
                            // Jika berhasil update password
                            Timber.d("Password berhasil diubah")
                            Snackbar.make(
                                layoutReset, "Password berhasil diubah", Snackbar.LENGTH_LONG
                            ).show()
                        }.addOnFailureListener { e -> // Jika gagal update password
                            Timber.w(e, "Password gagal diubah")
                            Snackbar.make(
                                layoutReset, "Password gagal diubah", Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }.addOnFailureListener { e ->
                        Timber.w(e, "Gagal re-autentikasi user")
                        Snackbar.make(layoutReset, "Gagal: " + e.localizedMessage,
                            Snackbar.LENGTH_LONG).show()
                    }
                }
                builder.setNegativeButton("Batal", null)
                builder.show()
            } else {
                binding.tiPasswordUlangReset.error = "Password tidak sama"
                return@setOnClickListener
            }
        }

        setContentView(binding.root)
    }
}