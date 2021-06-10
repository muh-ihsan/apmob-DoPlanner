package com.tubesApmob.doplanner

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tubesApmob.doplanner.databinding.DplFirstTimeSetupBinding
import timber.log.Timber

class FirstSetupActivity : BaseActivity() {
    private lateinit var binding: DplFirstTimeSetupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var layoutSetup: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DplFirstTimeSetupBinding.inflate(layoutInflater)
        auth = Firebase.auth
        val db = Firebase.firestore

        layoutSetup = binding.constraintSetup
        val menuIsijurusan = binding.menuIsijurusanSetup
        // Untuk mengisi menu jurusan
        val items = listOf("Teknik Komputer", "Teknik Telekomunikasi", "Teknik Elektro", "Teknik Fisika")
        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        menuIsijurusan.setAdapter(adapter)

        binding.btSubmitSetup.setOnClickListener {
            val user = auth.currentUser
            if (user != null) {
                val uid = user.uid
                var nama = user.displayName
                var email = user.email
                var jurusan = menuIsijurusan.text.toString()

                if (jurusan.isEmpty()) {
                    binding.menuJurusanSetup.error = "Wajib diisi"
                    return@setOnClickListener
                } else {
                    binding.menuJurusanSetup.error = null
                }

                var userProfile = hashMapOf(
                    "nama" to nama,
                    "email" to email,
                    "jurusan" to jurusan
                )

                db.collection("users").document(uid)
                    .set(userProfile)
                    .addOnSuccessListener {
                        Timber.d("Profil user berhasil dibuat")
                        keActivityMain(this)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Timber.w(e, "Error dalam membuat profil")
                        MaterialAlertDialogBuilder(baseContext)
                            .setTitle("Buat profil gagal.")
                            .setMessage("Anda akan kembali ke halaman login")
                            .setNegativeButton("Ok") { dialog, which ->
                                auth.signOut()
                                keActivityLogin(this)
                                finish()
                            }
                            .show()
                    }
            }
        }

        setContentView(binding.root)
    }
}