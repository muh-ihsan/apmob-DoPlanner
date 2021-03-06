package com.tubesApmob.doplanner

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
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
import com.tubesApmob.doplanner.databinding.DplTambahTugasBinding
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TambahTugasActivity : BaseActivity() {
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

        val layoutParent = binding.constraintTambahTugas
        val inputTugas = binding.inputNamaTugas
        val inputMatkul = binding.inputMatkulTugas
        val textTanggal = binding.tvTanggalTugas
        inputKelas = binding.inputKelasTugas
        val inputDeskripsi = binding.inputDeskripsiTugas
        val tugasRef = db.collection("users").document(user.uid).collection("tugas")

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

            tugasRef.add(dataTugas).addOnSuccessListener {
                Timber.d("Tugas ditambah dengan id:${it.id}")
                Toast.makeText(baseContext, "Tugas berhasil dibuat", Toast.LENGTH_SHORT)
                    .show()

                // Menambah data mahasiswa ke dalam tugas
                val kelasRef = db.collection("users").document(user.uid).collection("kelas")
                val mahasiswaCekRef = tugasRef.document(it.id).collection("mahasiswa")
                kelasRef.document(kelas).collection("mahasiswa").get()
                    .addOnSuccessListener { docs ->
                        Timber.d("Sukses mendapatkan data mahasiswa kelas")
                        for (doc in docs) {
                            var namaMahasiswa = doc.get("nama").toString()
                            var nimMahasiswa = doc.get("nim").toString()

                            val dataMahasiswa = hashMapOf(
                                "nama" to namaMahasiswa,
                                "nim" to nimMahasiswa,
                                "cek" to false
                            )
                            mahasiswaCekRef.document(nimMahasiswa).set(dataMahasiswa)
                                .addOnSuccessListener {
                                    Timber.d("Sukses menambah mahasiswa ke dalam tugas")
                                } .addOnFailureListener { e ->
                                    Timber.w(e, "Gagal menambah mahasiswa ke dalam tugas")
                                }
                        }
                    } .addOnFailureListener { e ->
                        Timber.w(e, "Gagal mendapatkan data mahasiswa kelas")
                    }

                finish()
            } .addOnFailureListener { e ->
                Timber.w(e, "Tugas gagal dibuat: ")
                Snackbar.make(layoutParent, "Tugas gagal dibuat: " + e.localizedMessage,
                    Snackbar.LENGTH_LONG).show()
            }
        }

        setContentView(binding.root)
    }

    private fun inputOnClick(input: TextInputEditText, layout: TextInputLayout) {
        input.setOnClickListener {
            layout.error = null
        }
    }
}