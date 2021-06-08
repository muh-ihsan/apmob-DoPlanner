package com.tubesApmob.doplanner

import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.tubesApmob.doplanner.databinding.DplDaftarMahasiswaBinding

class DaftarMahasiswaActivity : ActivityIntent(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: DplDaftarMahasiswaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DplDaftarMahasiswaBinding.inflate(layoutInflater)
        binding.dplNavigationViewMahasiswa.setCheckedItem(R.id.item_mahasiswa_navdrawer)
        binding.dplNavigationViewMahasiswa.setNavigationItemSelectedListener(this)

        binding.topAppBarMahasiswa.setOnClickListener {
            binding.drawerLayoutMahasiswa.open()
        }

        setContentView(binding.root)
    }

    // untuk navigasi menu yang ada di navdrawer
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_home_navdrawer -> { keActivityMain(this); finish() }
            R.id.item_jadwal_navdrawer -> { keActivityJadwal(this); finish() }
            R.id.item_tugas_navdrawer -> { keActivityTugas(this); finish() }
            R.id.item_mahasiswa_navdrawer -> binding.drawerLayoutMahasiswa.close()
        }
        binding.drawerLayoutMahasiswa.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (binding.drawerLayoutMahasiswa.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayoutMahasiswa.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}