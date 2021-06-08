package com.tubesApmob.doplanner

import android.os.Bundle
import com.tubesApmob.doplanner.databinding.DplTambahMahasiwaBinding

class TambahMahasiswaActivity : ActivityIntent() {
    private lateinit var binding: DplTambahMahasiwaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DplTambahMahasiwaBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}