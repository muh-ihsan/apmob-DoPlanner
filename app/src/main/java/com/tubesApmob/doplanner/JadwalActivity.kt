package com.tubesApmob.doplanner

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tubesApmob.doplanner.databinding.DplJadwalBinding
import timber.log.Timber

class JadwalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val matkulText: TextView = itemView.findViewById(R.id.tv_matkul_jadwal_card)
    val tanggalText: TextView = itemView.findViewById(R.id.tv_tanggal_jadwal_card)
    val mediaText: TextView = itemView.findViewById(R.id.tv_media_jadwal_card)
    val linkText: TextView = itemView.findViewById(R.id.tv_link_jadwal_card)
    val kelasText: TextView = itemView.findViewById(R.id.tv_kelas_jadwal_card)
    val jamText: TextView = itemView.findViewById(R.id.tv_jam_jadwal_card)
    val btHapus: ImageButton = itemView.findViewById(R.id.bt_hapus_jadwal_card)
    val btEdit: ImageButton = itemView.findViewById(R.id.bt_edit_jadwal_card)
}

class JadwalActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: DplJadwalBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: FirestoreRecyclerAdapter<DataJadwal, JadwalViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DplJadwalBinding.inflate(layoutInflater)
        auth = Firebase.auth
        user = Firebase.auth.currentUser!!
        db = Firebase.firestore

        // Set up adapter recyclerview
        val query = db.collection("users").document(user.uid).collection("jadwal")
            .orderBy("tanggal", Query.Direction.ASCENDING)
        val options = FirestoreRecyclerOptions.Builder<DataJadwal>().setQuery(query, DataJadwal::class.java)
            .setLifecycleOwner(this).build()
        adapter = object: FirestoreRecyclerAdapter<DataJadwal, JadwalViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JadwalViewHolder {
                val view = LayoutInflater.from(this@JadwalActivity)
                    .inflate(R.layout.dpl_jadwal_item, parent, false)
                return JadwalViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: JadwalViewHolder,
                position: Int,
                model: DataJadwal
            ) {
                holder.matkulText.text = model.matkul
                holder.tanggalText.text = model.tanggal
                holder.mediaText.text = model.media
                holder.linkText.text = model.link
                holder.kelasText.text = model.kelas
                holder.jamText.text = model.jam
                val jadwalID = snapshots.getSnapshot(position).id

                holder.btHapus.setOnClickListener {
                    Timber.d("Hapus di-tap pada ID:$jadwalID dan bernama ${holder.matkulText.text}")
                    val hapusAlert = MaterialAlertDialogBuilder(this@JadwalActivity)
                    hapusAlert.setMessage("Apakah anda yakin ingin menghapus jadwal ini?")
                    hapusAlert.setPositiveButton("Hapus") { dialog, i ->
                        db.collection("users").document(user.uid).collection("jadwal")
                            .document(jadwalID).delete()
                            .addOnSuccessListener {
                                Timber.d("Sukses menghapus jadwal")
                                Toast.makeText(baseContext, "Sukses menghapus jadwal", Toast.LENGTH_SHORT)
                                    .show()
                            } .addOnFailureListener { e ->
                                Timber.w(e, "Gagal menghapus jadwal")
                                Toast.makeText(baseContext, "Gagal menghapus jadwal" + e.localizedMessage,
                                    Toast.LENGTH_LONG).show()
                            }
                    }
                    hapusAlert.setNegativeButton("Batal", null)
                    hapusAlert.show()
                }
                holder.btEdit.setOnClickListener {
                    Timber.d("Edit di-tap pada ID:$jadwalID dan bernama ${holder.matkulText.text}")
                    val intentEditJadwal = Intent(this@JadwalActivity, EditJadwalActivity::class.java)
                        .putExtra("idJadwal", jadwalID)
                    startActivity(intentEditJadwal)
                }
            }

        }

        binding.dplNavigationViewJadwal.setCheckedItem(R.id.item_jadwal_navdrawer)
        binding.dplNavigationViewJadwal.setNavigationItemSelectedListener(this)

        val headerView = binding.dplNavigationViewJadwal.getHeaderView(0)
        val headerName: TextView = headerView.findViewById(R.id.tv_nama_navdrawer)
        val headerEmail: TextView = headerView.findViewById(R.id.tv_email_navdraw)
        setHeaderDrawerNavigation(headerName, headerEmail, db, auth)

        binding.topAppBarJadwal.setOnClickListener {
            binding.drawerLayoutJadwal.open()
        }

        binding.fabTambahJadwal.setOnClickListener {
            keActivityTambahJadwal(this)
        }

        setContentView(binding.root)

        binding.recyclerViewJadwal.adapter = adapter
        binding.recyclerViewJadwal.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    // untuk navigasi menu yang ada di navdrawer
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_home_navdrawer -> {
                val intentMain = Intent(this, MainActivity::class.java)
                intentMain.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intentMain)
                finish()
            }
            R.id.item_jadwal_navdrawer -> binding.drawerLayoutJadwal.close()
            R.id.item_tugas_navdrawer -> { keActivityTugas(this); finish() }
            R.id.item_aturAkun_navdrawer -> keActivityAturAkun(this)
            R.id.item_logout_navdrawer -> {
                auth.signOut()
                keActivityLogin(this)
                finish()
            }
        }
        binding.drawerLayoutJadwal.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (binding.drawerLayoutJadwal.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayoutJadwal.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}