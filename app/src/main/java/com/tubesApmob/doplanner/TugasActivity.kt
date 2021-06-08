package com.tubesApmob.doplanner

import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.tubesApmob.doplanner.databinding.DplTugasBinding

class TugasActivity : ActivityIntent(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: DplTugasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DplTugasBinding.inflate(layoutInflater)
        binding.dplNavigationViewTugas.setCheckedItem(R.id.item_tugas_navdrawer)
        binding.dplNavigationViewTugas.setNavigationItemSelectedListener(this)

        binding.topAppBarTugas.setOnClickListener {
            binding.drawerLayoutTugas.open()
        }

        setContentView(binding.root)
    }

    // untuk navigasi menu yang ada di navdrawer
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_home_navdrawer -> { keActivityMain(this); finish() }
            R.id.item_jadwal_navdrawer -> { keActivityJadwal(this); finish() }
            R.id.item_tugas_navdrawer -> binding.drawerLayoutTugas.close()
            R.id.item_mahasiswa_navdrawer -> { keActivityMahasiswa(this); finish() }
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