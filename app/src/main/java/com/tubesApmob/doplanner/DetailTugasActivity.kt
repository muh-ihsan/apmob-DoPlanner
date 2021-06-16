package com.tubesApmob.doplanner

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tubesApmob.doplanner.databinding.DplTugasDetailBinding
import timber.log.Timber

class DetailTugasActivity : BaseActivity() {
    private lateinit var binding: DplTugasDetailBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DplTugasDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        user = auth.currentUser!!
        db = Firebase.firestore

        val textTugas = binding.tvNamaTugasDetail
        val textKelas = binding.tvKelasTugasDetail
        val textMatkul = binding.tvMatkulTugasDetail
        val deadlineTugas = binding.tvTglDeadlineTugasDetail
        val idTugas = intent.getStringExtra("idTugasDetail")
        val tugasRef = db.collection("users").document(user.uid).collection("tugas")
        var deskripsiTugas: String = ""

        tugasRef.document(idTugas!!).get().addOnSuccessListener {
            Timber.d("Sukses ambil data tugas")
            textTugas.text = it.get("nama").toString()
            textMatkul.text = it.get("matkul").toString()
            textKelas.text = it.get("kelas").toString()
            deadlineTugas.text = it.get("tanggal").toString()
            deskripsiTugas = it.get("deskripsi").toString()
        } .addOnFailureListener { e ->
            Timber.w(e, "Gagal ambil data tugas: ")
        }

        binding.topAppBarDetailTugas.setOnClickListener {
            finish()
        }

        binding.btDeskripsiTugas.setOnClickListener {
            val deskripsiDialog = MaterialAlertDialogBuilder(this)
            deskripsiDialog.setTitle("Deskripsi Tugas")
            if (deskripsiTugas.isEmpty()) {
                deskripsiDialog.setMessage("Tidak ada deskripsi")
            } else {
                deskripsiDialog.setMessage(deskripsiTugas)
            }
            deskripsiDialog.setPositiveButton("OK", null)
            deskripsiDialog.show()
        }

        binding.topAppBarDetailTugas.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_hapus_tugas_detail -> {
                    Timber.d("Hapus pada ID:$idTugas")
                    val hapusAlert = MaterialAlertDialogBuilder(this)
                    hapusAlert.setMessage("Apakah anda yakin ingin menghapus tugas ini?")
                    hapusAlert.setPositiveButton("Hapus") { dialog, i ->
                        tugasRef.document(idTugas).delete()
                            .addOnSuccessListener {
                                Timber.d("Sukses menghapus tugas")
                                Toast.makeText(baseContext, "Sukses menghapus tugas", Toast.LENGTH_SHORT)
                                    .show()
                            } .addOnFailureListener { e ->
                                Timber.w(e, "Gagal menghapus tugas")
                                Toast.makeText(baseContext, "Gagal menghapus tugas" + e.localizedMessage,
                                    Toast.LENGTH_LONG).show()
                            }
                        finish()
                    }
                    hapusAlert.setNegativeButton("Batal", null)
                    hapusAlert.show()
                    true
                }
                R.id.menu_edit_tugas_detail -> {
                    val intentEditTugas = Intent(this, EditTugasActivity::class.java)
                        .putExtra("idTugas", idTugas)
                    startActivity(intentEditTugas)
                    true
                }
                else -> false
            }
        }

    }
}