package com.tubesApmob.doplanner

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
import com.tubesApmob.doplanner.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.dplNavigationView.setNavigationItemSelectedListener(this)
        auth = Firebase.auth
        db = Firebase.firestore

        // Untuk menu NavDrawer
        binding.topAppBarMain.setOnClickListener {
            binding.drawerLayoutMain.open()
        }

        // Untuk tombol jadwal
        binding.btJadwalMain.setOnClickListener {
            keActivityJadwal(this)
        }

        // Untuk tombol tugas
        binding.btTugasMain.setOnClickListener {
            keActivityTugas(this)
        }

        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()

        val headerView = binding.dplNavigationView.getHeaderView(0)
        val headerName: TextView = headerView.findViewById(R.id.tv_nama_navdrawer)
        val headerEmail: TextView = headerView.findViewById(R.id.tv_email_navdraw)

        setHeaderDrawerNavigation(headerName, headerEmail, db, auth)
        binding.dplNavigationView.setCheckedItem(R.id.item_home_navdrawer)
    }

    // untuk navigasi menu yang ada di navdrawer
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_home_navdrawer -> binding.drawerLayoutMain.close()
            R.id.item_jadwal_navdrawer -> keActivityJadwal(this)
            R.id.item_tugas_navdrawer -> keActivityTugas(this)
            R.id.item_aturAkun_navdrawer -> keActivityAturAkun(this)
            R.id.item_logout_navdrawer -> {
                auth.signOut()
                keActivityLogin(this)
                finish()
            }
        }
        binding.drawerLayoutMain.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (binding.drawerLayoutMain.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayoutMain.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}