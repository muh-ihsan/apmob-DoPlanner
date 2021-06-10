package com.tubesApmob.doplanner

import android.os.Bundle
import android.widget.ArrayAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tubesApmob.doplanner.databinding.DplPengaturanAkunBinding
import timber.log.Timber

class AturAkunActivity : BaseActivity() {
    private lateinit var binding: DplPengaturanAkunBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DplPengaturanAkunBinding.inflate(layoutInflater)
        auth = Firebase.auth
        db = Firebase.firestore
        val user = auth.currentUser

        val layoutAkun = binding.constraintAkun
        val inputNama = binding.inputNamaAkun
        val inputEmail = binding.inputEmailAkun
        val menuJurusan = binding.menuIsijurusanAkun

        binding.topAppBarPengaturan.setOnClickListener {
            finish()
        }

        val uid = user!!.uid
        db.collection("users").document(uid)
            .get().addOnSuccessListener {
                Timber.d("Ambil data profil sukses")
                inputNama.setText(it.get("nama").toString())
                inputEmail.setText(it.get("email").toString())
            }.addOnFailureListener { e ->
                Timber.w(e, "Ambil data profil gagal: ")
            }

        // Untuk mengisi menu jurusan
        val items =
            listOf("Teknik Komputer", "Teknik Telekomunikasi", "Teknik Elektro", "Teknik Fisika")
        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        menuJurusan.setAdapter(adapter)

        binding.btSubmitAkun.setOnClickListener {
            val nama = inputNama.text.toString()
            val email = inputEmail.text.toString().trim()
            val jurusan = menuJurusan.text.toString()

            if (nama.isEmpty()) {
                Snackbar.make(layoutAkun, "Nama tidak boleh kosong", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                Snackbar.make(layoutAkun, "Email tidak boleh kosong", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (jurusan.isEmpty()) {
                Snackbar.make(layoutAkun, "Jurusan tidak boleh kosong", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val profil = hashMapOf(
                "nama" to nama,
                "email" to email,
                "jurusan" to jurusan
            )

            // Menampilkan dialog autentikasi password
            val builder = MaterialAlertDialogBuilder(this)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.dpl_alert_reautentikasi, null)
            val inputPassword = dialogLayout.findViewById<TextInputEditText>(R.id.input_password_alert)
            builder.setTitle("Masukkan Password Saat Ini")
            builder.setView(dialogLayout)
            builder.setPositiveButton("Submit") { dialog, i ->
                val password = inputPassword.text.toString()
                val credential = EmailAuthProvider.getCredential(user.email!!, password)

                // Re-autentikasi user
                user.reauthenticate(credential)
                    .addOnSuccessListener {
                        // Jika berhasil diautentikasi
                        Timber.d("Sukses re-autentikasi user")
                        user.updateEmail(email).addOnCompleteListener { // Email diubah
                            // Jika email berhasil diubah
                            Timber.d("Email akun diupdate")

                            // Database profil diubah
                            db.collection("users").document(uid).set(profil)
                                .addOnSuccessListener {
                                    // Jika profil sukses di update
                                    Timber.d("Profil di update")
                                    Snackbar.make(layoutAkun, "Profil sukses di update", Snackbar.LENGTH_LONG)
                                        .show()
                                } .addOnFailureListener { e ->
                                    // Jika profil gagal di update
                                    Timber.w(e, "Profil gagal diupdate: ")
                                    Snackbar.make(layoutAkun, "Profil gagal diupdate: " + e.localizedMessage,
                                        Snackbar.LENGTH_LONG).show()
                                }
                        }
                    }.addOnFailureListener { e -> // Jika gagal re-autentikasi user
                        Timber.w(e, "Gagal re-autentikasi user")
                    }
            }
            builder.setNegativeButton("Batal", null)
            builder.show()

        }

        binding.btResetpasswordAkun.setOnClickListener {
            keResetPassword(this)
        }

        setContentView(binding.root)
    }
}