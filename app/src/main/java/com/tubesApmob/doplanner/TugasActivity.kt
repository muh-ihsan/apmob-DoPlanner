package com.tubesApmob.doplanner

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tubesApmob.doplanner.databinding.DplTugasBinding

class TugasActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: DplTugasBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DplTugasBinding.inflate(layoutInflater)
        auth = Firebase.auth
        db = Firebase.firestore

        binding.dplNavigationViewTugas.setCheckedItem(R.id.item_tugas_navdrawer)
        binding.dplNavigationViewTugas.setNavigationItemSelectedListener(this)

        val headerView = binding.dplNavigationViewTugas.getHeaderView(0)
        val headerName: TextView = headerView.findViewById(R.id.tv_nama_navdrawer)
        val headerEmail: TextView = headerView.findViewById(R.id.tv_email_navdraw)
        setHeaderDrawerNavigation(headerName, headerEmail, db, auth)

        binding.topAppBarTugas.setOnClickListener {
            binding.drawerLayoutTugas.open()
        }

        setContentView(binding.root)
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