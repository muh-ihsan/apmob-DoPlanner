package com.tubesApmob.doplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tubesApmob.doplanner.databinding.ActivityMainBinding
import javax.annotation.meta.When

class MainActivity : ActivityIntent(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.dplNavigationView.setCheckedItem(R.id.item_home_navdrawer)
        binding.dplNavigationView.setNavigationItemSelectedListener(this)

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

        // Untuk tombol daftar mahasiswa
        binding.btMahasiswaMain.setOnClickListener {
            keActivityMahasiswa(this)
        }


        setContentView(binding.root)
    }

    // untuk navigasi menu yang ada di navdrawer
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_home_navdrawer -> binding.drawerLayoutMain.close()
            R.id.item_jadwal_navdrawer -> keActivityJadwal(this)
            R.id.item_tugas_navdrawer -> keActivityTugas(this)
            R.id.item_mahasiswa_navdrawer -> keActivityMahasiswa(this)
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