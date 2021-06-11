package com.tubesApmob.doplanner

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tubesApmob.doplanner.databinding.DplTambahJadwalBinding
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TambahJadwalActivity : BaseActivity() {
    private lateinit var binding: DplTambahJadwalBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var inputKelas: AutoCompleteTextView
    private lateinit var kelasList: ArrayList<String>
    private lateinit var user: FirebaseUser

    @SuppressLint("SetTextI18n", "BinaryOperationInTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DplTambahJadwalBinding.inflate(layoutInflater)
        auth = Firebase.auth
        db = Firebase.firestore
        user = auth.currentUser!!

        val layoutParent = binding.constraintTambahJadwal
        val inputMatkul = binding.inputMatkulJadwal
        inputKelas = binding.inputKelasJadwal
        val textTanggal = binding.tvTanggalJadwal
        val textJam = binding.tvJamJadwal
        val inputMedia = binding.inputMediaBelajarJadwal
        val inputLink = binding.inputLinkBelajarJadwal

        binding.topAppBarTambahJadwal.setOnClickListener {
            finish()
        }

        binding.btJamJadwal.setOnClickListener {
            val cal = Calendar.getInstance()
            val pilihJam = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(cal.get(Calendar.HOUR_OF_DAY))
                .setMinute(cal.get(Calendar.MINUTE))
                .setTitleText("Pilih Waktu Mulai")
                .build()
            pilihJam.show(supportFragmentManager, "show_tag")
            pilihJam.addOnPositiveButtonClickListener {
                cal.set(Calendar.HOUR_OF_DAY, pilihJam.hour)
                cal.set(Calendar.MINUTE, pilihJam.minute)
                textJam.text = SimpleDateFormat("HH:mm").format(cal.time)
            }

            // Kode untuk TimePickerDialog biasa
            /*val pilihJam = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                textJam.text = SimpleDateFormat("HH:mm").format(cal.time)
            }
            TimePickerDialog(this, pilihJam, cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE), true).show()*/
        }

        binding.btTambahKelasJadwal.setOnClickListener {
            val alert = MaterialAlertDialogBuilder(this)
            val dialogLayout = layoutInflater.inflate(R.layout.dpl_alert_tambah_kelas, null)
            val inputKelasAlert = dialogLayout.findViewById<TextInputEditText>(R.id.input_kelas_alert)
            alert.setTitle("Masukkan Kelas Baru")
            alert.setView(dialogLayout)
            alert.setPositiveButton("Tambah") { dialog, i ->
                val kelasAlert = inputKelasAlert.text.toString()
                val kelasRef = db.collection("users").document(user.uid).collection("kelas")
                val kelasMap = hashMapOf(
                    "kelas" to kelasAlert
                )

                kelasRef.orderBy("kelas", Query.Direction.ASCENDING).get()
                    .addOnSuccessListener { docs ->
                        for (doc in docs) {
                            val kelasSort = doc.get("kelas").toString()
                            if (kelasSort == kelasAlert) {
                                Timber.d("Tidak bisa menambah kelas, sudah terdaftar")
                                Snackbar.make(layoutParent, "Tidak bisa menambah kelas, sudah terdaftar",
                                    Snackbar.LENGTH_SHORT).show()
                                break
                            } else {
                                kelasList.add(kelasAlert)
                                inputKelas.setText(kelasAlert, false)
                                kelasRef.add(kelasMap)
                                    .addOnSuccessListener {
                                        Timber.d("Sukses menambah kelas $kelasAlert ke Firebase dengan id: ${it.id}")
                                    } .addOnFailureListener { e ->
                                        Timber.w(e, "Gagal menambah kelas $kelasAlert: ")
                                    }
                                Timber.d("Kelas $kelasAlert ditambah ke menu")
                            }
                        }
                    }
            }
            alert.setNegativeButton("Batal", null)
            alert.show()
        }

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

        // Set text input error jadi null jika di-tap
        inputOnClick(inputMatkul, binding.tiMatkulJadwal)
        inputOnClick(inputMedia, binding.tiMediaBelajarJadwal)
        inputOnClick(inputLink, binding.tiLinkBelajarJadwal)

        binding.btTambahJadwal.setOnClickListener {
            val matkul = inputMatkul.text.toString()
            val kelas = inputKelas.text.toString()
            val tanggalText = textTanggal.text
            val jamText = textJam.text
            val mediaBelajar = inputMedia.text.toString()
            val linkBelajar = inputLink.text.toString()

            // Jika input kosong
            if (matkul.isEmpty()) {
                binding.tiMatkulJadwal.error = getString(R.string.dpl_forbid_empty)
                return@setOnClickListener
            }
            if (kelas.isEmpty()) {
                Snackbar.make(layoutParent, "Kelas tidak boleh kosong", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (tanggalText == getString(R.string.dpl_tanggal)) {
                Snackbar.make(layoutParent, "Tanggal tidak boleh kosong", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (jamText == getString(R.string.dpl_jam)) {
                Snackbar.make(layoutParent, "Jam tidak boleh kosong", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (mediaBelajar.isEmpty()) {
                binding.tiMediaBelajarJadwal.error = getString(R.string.dpl_forbid_empty)
                return@setOnClickListener
            }

            Timber.d("Matkul: $matkul, Kelas: $kelas, tanggal: $tanggalText, waktu: $jamText, " +
                    "media belajar: $mediaBelajar, link belajar: $linkBelajar")

            val dataJadwal = hashMapOf(
                "matkul" to matkul,
                "kelas" to kelas,
                "jam" to jamText,
                "tanggal" to tanggalText,
                "media" to mediaBelajar,
                "link" to linkBelajar
            )

            db.collection("users").document(user.uid).collection("jadwal")
                .add(dataJadwal)
                .addOnSuccessListener {
                    Timber.d("Jadwal dibuat dengan ID: ${it.id}")
                    Toast.makeText(baseContext, "Jadwal berhasil dibuat", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                } .addOnFailureListener { e ->
                    Timber.w(e, "Jadwal gagal dibuat dengan: ")
                    Snackbar.make(layoutParent, "Jadwal gagal dibuat: " + e.localizedMessage,
                        Snackbar.LENGTH_LONG).show()
                }
        }

        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        kelasList = ArrayList()
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
    }

    private fun inputOnClick(input: TextInputEditText, layout: TextInputLayout) {
        input.setOnClickListener {
            layout.error = null
        }
    }
}