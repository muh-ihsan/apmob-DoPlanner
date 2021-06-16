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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tubesApmob.doplanner.databinding.DplTugasBinding
import timber.log.Timber

class TugasViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tugasText: TextView = itemView.findViewById(R.id.tv_nama_tugas_card)
    val matkulText: TextView = itemView.findViewById(R.id.tv_matkul_tugas_card)
    val deadlineText: TextView = itemView.findViewById(R.id.tv_tgl_deadline_tugas_card)
    val kelasText: TextView = itemView.findViewById(R.id.tv_kelas_tugas_card)
    val btEdit: ImageButton = itemView.findViewById(R.id.bt_edit_tugas_card)
    val btHapus: ImageButton = itemView.findViewById(R.id.bt_hapus_tugas_card)
}

class TugasActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: DplTugasBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: FirestoreRecyclerAdapter<DataTugas, TugasViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DplTugasBinding.inflate(layoutInflater)
        auth = Firebase.auth
        val user = auth.currentUser
        db = Firebase.firestore

        val query = db.collection("users").document(user!!.uid).collection("tugas")
            .orderBy("tanggal", Query.Direction.ASCENDING)
        val options = FirestoreRecyclerOptions.Builder<DataTugas>().setQuery(query, DataTugas::class.java)
            .setLifecycleOwner(this).build()
        adapter = object: FirestoreRecyclerAdapter<DataTugas, TugasViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TugasViewHolder {
                val view = LayoutInflater.from(this@TugasActivity)
                    .inflate(R.layout.dpl_tugas_item, parent, false)
                return TugasViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: TugasViewHolder,
                position: Int,
                model: DataTugas
            ) {
                holder.tugasText.text = model.nama
                holder.matkulText.text = model.matkul
                holder.deadlineText.text = model.tanggal
                holder.kelasText.text = model.kelas
                val tugasID = snapshots.getSnapshot(position).id

                holder.itemView.setOnClickListener {
                    val intentDetailTugas = Intent(this@TugasActivity, DetailTugasActivity::class.java)
                        .putExtra("idTugasDetail", tugasID)
                    startActivity(intentDetailTugas)
                }

                /*holder.btHapus.setOnClickListener {
                    Timber.d("Hapus di-tap pada ID:$tugasID dan bernama ${holder.matkulText.text}")
                    val hapusAlert = MaterialAlertDialogBuilder(this@TugasActivity)
                    hapusAlert.setMessage("Apakah anda yakin ingin menghapus tugas ini?")
                    hapusAlert.setPositiveButton("Hapus") { dialog, i ->
                        db.collection("users").document(user.uid).collection("tugas")
                            .document(tugasID).delete()
                            .addOnSuccessListener {
                                Timber.d("Sukses menghapus tugas")
                                Toast.makeText(baseContext, "Sukses menghapus tugas", Toast.LENGTH_SHORT)
                                    .show()
                            } .addOnFailureListener { e ->
                                Timber.w(e, "Gagal menghapus tugas")
                                Toast.makeText(baseContext, "Gagal menghapus tugas" + e.localizedMessage,
                                    Toast.LENGTH_LONG).show()
                            }
                    }
                    hapusAlert.setNegativeButton("Batal", null)
                    hapusAlert.show()
                }
                holder.btEdit.setOnClickListener {
                    Timber.d("Edit di-tap pada ID:$tugasID dan bernama ${holder.matkulText.text}")
                    val intentEditTugas = Intent(this@TugasActivity, EditTugasActivity::class.java)
                        .putExtra("idTugas", tugasID)
                    startActivity(intentEditTugas)
                }*/
            }

        }

        binding.dplNavigationViewTugas.setCheckedItem(R.id.item_tugas_navdrawer)
        binding.dplNavigationViewTugas.setNavigationItemSelectedListener(this)

        val headerView = binding.dplNavigationViewTugas.getHeaderView(0)
        val headerName: TextView = headerView.findViewById(R.id.tv_nama_navdrawer)
        val headerEmail: TextView = headerView.findViewById(R.id.tv_email_navdraw)
        setHeaderDrawerNavigation(headerName, headerEmail, db, auth)

        binding.topAppBarTugas.setOnClickListener {
            binding.drawerLayoutTugas.open()
        }

        binding.fabTambahTugas.setOnClickListener {
            keActivityTambahTugas(this)
        }

        setContentView(binding.root)

        binding.recyclerViewTugas.adapter = adapter
        binding.recyclerViewTugas.layoutManager = LinearLayoutManager(this)
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
            R.id.item_jadwal_navdrawer -> { keActivityJadwal(this); finish() }
            R.id.item_tugas_navdrawer -> binding.drawerLayoutTugas.close()
            R.id.item_aturAkun_navdrawer -> keActivityAturAkun(this)
            R.id.item_logout_navdrawer -> {
                auth.signOut()
                keActivityLogin(this)
                finish()
            }
        }
        binding.drawerLayoutTugas.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (binding.drawerLayoutTugas.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayoutTugas.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}