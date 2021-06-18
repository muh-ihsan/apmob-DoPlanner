package com.tubesApmob.doplanner

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tubesApmob.doplanner.databinding.DplTugasDetailBinding
import org.w3c.dom.Text
import timber.log.Timber

class MahasiswaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val namaMahasiswa: TextView = itemView.findViewById(R.id.tv_list_nama_mahasiswa)
    val nimMahasiswa: TextView = itemView.findViewById(R.id.tv_list_nim_mahasiswa)
    val cekMahasiswa: MaterialCheckBox = itemView.findViewById(R.id.check_mahasiswa_list)
    val btHapusMahasiswa: ImageButton = itemView.findViewById(R.id.bt_hapus_mahasiswa_list)
    val btEditMahasiswa: ImageButton = itemView.findViewById(R.id.bt_edit_mahasiswa_list)
}

class DetailTugasActivity : BaseActivity() {
    private lateinit var binding: DplTugasDetailBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var user: FirebaseUser
    private lateinit var adapter: FirestoreRecyclerAdapter<DataMahasiswa, MahasiswaViewHolder>
    private lateinit var textTugas: TextView
    private lateinit var textMatkul: TextView
    private lateinit var textKelas: TextView
    private lateinit var deadlineTugas: TextView
    private lateinit var deskripsiTugas: String
    private lateinit var tugasRef: CollectionReference
    private lateinit var idTugas: String
    private lateinit var kelas: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DplTugasDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        user = auth.currentUser!!
        db = Firebase.firestore

        textTugas = binding.tvNamaTugasDetail
        textKelas = binding.tvKelasTugasDetail
        textMatkul = binding.tvMatkulTugasDetail
        deadlineTugas = binding.tvTglDeadlineTugasDetail
        idTugas = intent.getStringExtra("idTugasDetail").toString()
        tugasRef = db.collection("users").document(user.uid).collection("tugas")
        val kelasRef = db.collection("users").document(user.uid).collection("kelas")
        val mahasiswaCekRef = db.collection("users").document(user.uid).collection("tugas")
            .document(idTugas!!).collection("mahasiswa")

        // Untuk sinkronisasi data mahasiswa tugas dengan data mahasiswa pada induk
        binding.btSyncTugas.setOnClickListener {
            db.collection("users").document(user.uid).collection("kelas").document(kelas)
                .collection("mahasiswa").get().addOnSuccessListener { docs ->
                    Timber.d("Sukses ambil data mahasiswa kelas")
                    for (doc in docs) {
                        var namaMahasiswaKelas = doc.get("nama").toString()
                        var nimMahasiswaKelas = doc.get("nim").toString()
                        var dataMahaiswaKelas = hashMapOf(
                            "nama" to namaMahasiswaKelas,
                            "nim" to nimMahasiswaKelas,
                        )

                        mahasiswaCekRef.document(nimMahasiswaKelas).set(dataMahaiswaKelas, SetOptions.merge())
                            .addOnSuccessListener {
                                Timber.d("Sukses sinkron mahasiswa dengan NIM $nimMahasiswaKelas")
                            } .addOnFailureListener { e ->
                                Timber.w(e, "Gagal sinkron mahasiswa dengan NIM $nimMahasiswaKelas")
                            }
                    }
                } .addOnFailureListener { e ->
                    Timber.w(e, "Gagal ambil data kelas mahasiswa")
                }
        }

        val queryMahasiswa = db.collection("users").document(user.uid).collection("tugas")
            .document(idTugas).collection("mahasiswa").orderBy("nim", Query.Direction.ASCENDING)
        val options = FirestoreRecyclerOptions.Builder<DataMahasiswa>().setQuery(queryMahasiswa, DataMahasiswa::class.java)
            .setLifecycleOwner(this).build()
        adapter = object: FirestoreRecyclerAdapter<DataMahasiswa, MahasiswaViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MahasiswaViewHolder {
                val view = LayoutInflater.from(this@DetailTugasActivity)
                    .inflate(R.layout.dpl_daftar_mahasiswa_list, parent, false)
                return MahasiswaViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: MahasiswaViewHolder,
                position: Int,
                model: DataMahasiswa
            ) {
                holder.namaMahasiswa.text = model.nama
                holder.nimMahasiswa.text = model.nim
                holder.cekMahasiswa.isChecked = model.cek
                val idMahasiswa = snapshots.getSnapshot(position).id

                holder.cekMahasiswa.setOnClickListener {
                    if (holder.cekMahasiswa.isChecked) {
                        val data = hashMapOf("cek" to true)
                        mahasiswaCekRef.document(idMahasiswa).set(data, SetOptions.merge())
                            .addOnSuccessListener {
                                Timber.d("Mahasiswa dengan NIM $idMahasiswa diceklis")
                            } .addOnFailureListener { e ->
                                Timber.w(e, "Mahasiswa dengan NIM $idMahasiswa gagal diceklis")
                            }
                    } else {
                        val data = hashMapOf("cek" to false)
                        mahasiswaCekRef.document(idMahasiswa).set(data, SetOptions.merge())
                            .addOnSuccessListener {
                                Timber.d("Mahasiswa dengan NIM $idMahasiswa di unceklis")
                            } .addOnFailureListener { e ->
                                Timber.w(e, "Mahasiswa dengan NIM $idMahasiswa gagal di unceklis")
                            }
                    }
                }

                holder.btEditMahasiswa.setOnClickListener {
                    Timber.d("Edit di-tap pada ID:$idMahasiswa dan bernama ${holder.nimMahasiswa.text}")
                    val editMahasiswaDialog = MaterialAlertDialogBuilder(this@DetailTugasActivity)
                    val mahasiswaInflater = layoutInflater.inflate(R.layout.dpl_alert_mahasiswa, null)
                    val inputNamaMahasiswa = mahasiswaInflater.findViewById<TextInputEditText>(R.id.input_mahasiswa_alert)
                    val inputNimMahasiswa = mahasiswaInflater.findViewById<TextInputEditText>(R.id.input_nim_alert)

                    // Untuk mengisi data mahasiswa sebelum edit ke textedit
                    mahasiswaCekRef.document(idMahasiswa).get().addOnSuccessListener {
                        Timber.d("Ambil data mahasiswa cek berhasil")
                        inputNamaMahasiswa.setText(it.get("nama").toString())
                        inputNimMahasiswa.setText(it.get("nim").toString())
                    } .addOnFailureListener { e ->
                        Timber.w(e, "Ambil data mahasiswa gagal")
                    }

                    editMahasiswaDialog.setTitle("Tambah Mahasiswa")
                    editMahasiswaDialog.setView(mahasiswaInflater)
                    editMahasiswaDialog.setPositiveButton("Edit") { dialog, i ->
                        var namaMahasiswa = inputNamaMahasiswa.text.toString()
                        var nimMahasiswa = inputNimMahasiswa.text.toString()

                        val dataEdit = hashMapOf(
                            "nama" to namaMahasiswa,
                            "nim" to nimMahasiswa
                        )

                        // Untuk edit mahasiswa
                        kelasRef.document(kelas).collection("mahasiswa").document(nimMahasiswa).set(dataEdit, SetOptions.merge())
                            .addOnSuccessListener {
                                Timber.d("Edit mahasiswa berhasil")
                                Toast.makeText(baseContext, "Edit mahasiswa berhasil", Toast.LENGTH_SHORT)
                                    .show()
                            } .addOnFailureListener { e ->
                                Timber.w(e, "Gagal edit mahasiswa: ")
                                Toast.makeText(baseContext, "Gagal edit: " + e.localizedMessage, Toast.LENGTH_SHORT)
                                    .show()
                            }

                        // Untuk menghapus data mahasiswa sebelumnya
                        kelasRef.document(kelas).collection("mahasiswa").document(idMahasiswa).delete()
                            .addOnSuccessListener {
                                Timber.d("Hapus untuk mahasiswa sebelumnya berhasil")
                            } .addOnFailureListener { e ->
                                Timber.w(e, "Hapus untuk mahasiswa sebelumnya gagal")
                            }

                        // Untuk edit data mahasiswa
                        mahasiswaCekRef.document(nimMahasiswa).set(dataEdit, SetOptions.merge())
                            .addOnCompleteListener {
                                Timber.d("Edit mahasiswa cek berhasil")
                            } .addOnFailureListener { e ->
                                Timber.w(e, "Gagal menambah mahasiswa cek: ")
                            }

                        // Untuk menghapus data mahasiswa sebelumnya
                        mahasiswaCekRef.document(idMahasiswa).delete()
                            .addOnSuccessListener {
                                Timber.d("Hapus untuk mahasiswa cek sebelumnya berhasil")
                            } .addOnFailureListener { e ->
                                Timber.w(e, "Hapus untuk mahasiswa cek sebelumnya gagal")
                            }
                    }
                    editMahasiswaDialog.setNegativeButton("Batal", null)
                    editMahasiswaDialog.show()
                }

                holder.btHapusMahasiswa.setOnClickListener {
                    Timber.d("Hapus di-tap pada ID:$idMahasiswa dan bernama ${holder.nimMahasiswa.text}")
                    val hapusMahasiswaAlert = MaterialAlertDialogBuilder(this@DetailTugasActivity)
                    hapusMahasiswaAlert.setMessage("Apakah anda yakin ingin menghapus mahasiswa ini?")
                    hapusMahasiswaAlert.setPositiveButton("Hapus") { dialog, i ->
                        kelasRef.document(kelas).collection("mahasiswa").document(idMahasiswa).delete()
                            .addOnSuccessListener {
                                Timber.d("Hapus mahasiswa berhasil")
                                Toast.makeText(baseContext, "Hapus mahasiswa berhasil", Toast.LENGTH_SHORT)
                                    .show()
                            } .addOnFailureListener { e ->
                                Timber.w(e, "Gagal hapus mahasiswa: ")
                                Toast.makeText(baseContext, "Gagal hapus: " + e.localizedMessage, Toast.LENGTH_LONG)
                                    .show()
                            }

                        mahasiswaCekRef.document(idMahasiswa).delete()
                            .addOnSuccessListener {
                                Timber.d("Hapus mahasiswa cek berhasil")
                            } .addOnFailureListener { e ->
                                Timber.w(e, "Hapus mahasiswa cek gagal")
                            }
                    }
                    hapusMahasiswaAlert.setNegativeButton("Batal", null)
                    hapusMahasiswaAlert.show()
                }
            }

        }

        binding.recyclerViewTugasDetail.adapter = adapter
        binding.recyclerViewTugasDetail.layoutManager = LinearLayoutManager(this)

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

        binding.fabTambahMahasiswa.setOnClickListener {
            val tambahMahasiswaDialog = MaterialAlertDialogBuilder(this)
            val mahasiswaInflater = layoutInflater.inflate(R.layout.dpl_alert_mahasiswa, null)
            val inputNamaMahasiswa = mahasiswaInflater.findViewById<TextInputEditText>(R.id.input_mahasiswa_alert)
            val inputNimMahasiswa = mahasiswaInflater.findViewById<TextInputEditText>(R.id.input_nim_alert)
            tambahMahasiswaDialog.setTitle("Tambah Mahasiswa")
            tambahMahasiswaDialog.setView(mahasiswaInflater)
            tambahMahasiswaDialog.setPositiveButton("Tambah") { dialog, i ->
                var namaMahasiswa = inputNamaMahasiswa.text.toString()
                var nimMahasiswa = inputNimMahasiswa.text.toString()

                kelasRef.document(kelas).collection("mahasiswa").document(nimMahasiswa).get()
                    .addOnSuccessListener {
                        if (it.exists()) {
                            Timber.d("Tidak bisa menambah mahasiswa, sudah ada")
                            Toast.makeText(baseContext, "Tidak bisa menambah mahasiswa, sudah ada", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            val mahasiswaData = hashMapOf(
                                "nama" to namaMahasiswa,
                                "nim" to nimMahasiswa
                            )

                            val mahasiswaDataCek = hashMapOf(
                                "nama" to namaMahasiswa,
                                "nim" to nimMahasiswa,
                                "cek" to false
                            )
                            kelasRef.document(kelas).collection("mahasiswa").document(nimMahasiswa).set(mahasiswaData)
                                .addOnCompleteListener {
                                    Timber.d("Tambah mahasiswa berhasil")
                                    Toast.makeText(baseContext, "Tambah mahasiswa berhasil", Toast.LENGTH_SHORT)
                                        .show()
                                } .addOnFailureListener { e ->
                                    Timber.w(e, "Gagal menambah mahasiswa: ")
                                    Toast.makeText(baseContext, "Gagal: " + e.localizedMessage, Toast.LENGTH_SHORT)
                                        .show()
                                }

                            mahasiswaCekRef.document(nimMahasiswa).set(mahasiswaDataCek)
                                .addOnCompleteListener {
                                    Timber.d("Tambah mahasiswa cek berhasil")
                                } .addOnFailureListener { e ->
                                    Timber.w(e, "Gagal menambah mahasiswa cek: ")
                                }
                        }
                    } .addOnFailureListener { e ->
                        Timber.w(e, "Gagal mengambil data mahasiswa")
                    }
            }
            tambahMahasiswaDialog.setNegativeButton("Batal", null)
            tambahMahasiswaDialog.show()
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

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onResume() {
        super.onResume()
        tugasRef.document(idTugas).get().addOnSuccessListener {
            Timber.d("Sukses ambil data tugas")
            textTugas.text = it.get("nama").toString()
            textMatkul.text = it.get("matkul").toString()
            textKelas.text = it.get("kelas").toString()
            deadlineTugas.text = it.get("tanggal").toString()
            deskripsiTugas = it.get("deskripsi").toString()
        } .addOnFailureListener { e ->
            Timber.w(e, "Gagal ambil data tugas: ")
        }
        kelas = textKelas.text.toString()
        if (kelas.isEmpty()) {
            Timber.d("Isi kelas kosong")
        }
    }


    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}