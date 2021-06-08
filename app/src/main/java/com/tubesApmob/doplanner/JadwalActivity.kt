package com.tubesApmob.doplanner

import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.tubesApmob.doplanner.databinding.DplJadwalBinding

class JadwalActivity : ActivityIntent(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: DplJadwalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DplJadwalBinding.inflate(layoutInflater)

        binding.dplNavigationViewJadwal.setCheckedItem(R.id.item_jadwal_navdrawer)
        binding.dplNavigationViewJadwal.setNavigationItemSelectedListener(this)

        binding.topAppBarJadwal.setOnClickListener {
            binding.drawerLayoutJadwal.open()
        }

        binding.fabTambahJadwal.setOnClickListener {
            keActivityTambahJadwal(this)
        }

        setContentView(binding.root)
    }

    // untuk navigasi menu yang ada di navdrawer
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_home_navdrawer -> { keActivityMain(this); finish() }
            R.id.item_jadwal_navdrawer -> binding.drawerLayoutJadwal.close()
            R.id.item_tugas_navdrawer -> { keActivityTugas(this); finish() }
            R.id.item_mahasiswa_navdrawer -> { keActivityMahasiswa(this); finish() }
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