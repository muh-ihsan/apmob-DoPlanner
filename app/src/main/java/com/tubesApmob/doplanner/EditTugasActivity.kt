package com.tubesApmob.doplanner

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tubesApmob.doplanner.databinding.DplTambahJadwalBinding
import com.tubesApmob.doplanner.databinding.DplTambahTugasBinding
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EditTugasActivity : AppCompatActivity() {
    private lateinit var binding: DplTambahTugasBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var user: FirebaseUser
    private lateinit var inputKelas: AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DplTambahTugasBinding.inflate(layoutInflater)
        auth = Firebase.auth
        user = auth.currentUser!!
        db = Firebase.firestore
        setContentView(binding.root)

        val layoutParent = binding.constraintTambahTugas
        val inputTugas = binding.inputNamaTugas
        val inputMatkul = binding.inputMatkulTugas
        val textTanggal = binding.tvTanggalTugas
        inputKelas = binding.inputKelasTugas
        val inputDeskripsi = binding.inputDeskripsiTugas
        val tugasRef = db.collection("users").document(user.uid).collection("tugas")
        val idTugas = intent.getStringExtra("idTugas")

        tugasRef.document(idTugas!!).get().addOnSuccessListener {
            Timber.d("Sukses ambil data tugas")
            inputTugas.setText(it.get("nama").toString())
            inputMatkul.setText(it.get("matkul").toString())
            inputKelas.setText(it.get("kelas").toString(), false)
            textTanggal.text = it.get("tanggal").toString()
            inputDeskripsi.setText(it.get("deskripsi").toString())
        } .addOnFailureListener { e ->
            Timber.w(e, "Gagal ambil data tugas: ")
        }

        binding.btTambahTugas.text = getString(R.string.dpl_edit_tugas)
        binding.topAppBarTambahTugas.title = getString(R.string.dpl_edit_tugas)
        binding.topAppBarTambahTugas.setOnClickListener {
            finish()
        }

        // Set menu kelas
        var kelasList = ArrayList<String>()
        val kelasRef = db.collection("users").document(user.uid).collection("kelas")
        kelasRef.orderBy("kelas", Query.Direction.ASCENDING).get()
            .addOnSuccessListener { docs ->
                for (doc in docs) {
                    var kelasdoc = doc.get("kelas").toString()
                    kelasList.add(kelasdoc)
                    Timber.d("Kelas: ${kelasdoc}")
                }
            } .addOnFailureListener { e ->
                Timber.w(e, "Gagal ambil data.")
            }
        val kelasAdapter = ArrayAdapter(this, R.layout.list_item, kelasList)
        inputKelas.setAdapter(kelasAdapter)

        binding.btTanggalJadwal.setOnClickListener {
            val cal = Calendar.getInstance()
            val tanggalListener =
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    cal.set(Calendar.MONTH, month)
                    cal.set(Calendar.YEAR, year)
                    textTanggal.text = SimpleDateFormat("dd/MM/yyyy").format(cal.time)
                }
            DatePickerDialog(
                this, tanggalListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        inputOnClick(inputTugas, binding.tiNamaTugas)
        inputOnClick(inputMatkul, binding.tiMatkulTugas)

        binding.btTambahTugas.setOnClickListener {
            val namaTugas = inputTugas.text.toString()
            val tanggal = textTanggal.text
            val matkul = inputMatkul.text.toString()
            val kelas = inputKelas.text.toString()
            val deskripsi = inputDeskripsi.text.toString()

            // jika input kosong
            if (namaTugas.isEmpty()) {
                binding.tiNamaTugas.error = getString(R.string.dpl_forbid_empty)
                return@setOnClickListener
            }
            if (matkul.isEmpty()) {
                binding.tiMatkulTugas.error = getString(R.string.dpl_forbid_empty)
                return@setOnClickListener
            }
            if (kelas.isEmpty()) {
                Snackbar.make(layoutParent, "Kelas tidak boleh kosong", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (tanggal == getString(R.string.dpl_tv_tanggal_deadline)) {
                Snackbar.make(layoutParent, "Tanggal tidak boleh kosong", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            Timber.d("tugas: $namaTugas, matkul:$matkul, kelas: $kelas, tangggal: $tanggal, deskripsi: $deskripsi")

            val dataTugas = hashMapOf(
                "nama" to namaTugas,
                "matkul" to matkul,
                "kelas" to kelas,
                "tanggal" to tanggal,
                "deskripsi" to deskripsi
            )

            tugasRef.document(idTugas).set(dataTugas)
                .addOnSuccessListener {
                    Timber.d("Tugas berhasil di edit")
                    Toast.makeText(baseContext, "Tugas berhasil diedit", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                } .addOnFailureListener { e ->
                    Timber.w(e, "Tugas gagal diedit dengan: ")
                    Snackbar.make(layoutParent, "Tugas gagal diedit: " + e.localizedMessage,
                        Snackbar.LENGTH_LONG).show()
                }
        }
    }

    private fun inputOnClick(input: TextInputEditText, layout: TextInputLayout) {
        input.setOnClickListener {
            layout.error = null
        }
    }
}