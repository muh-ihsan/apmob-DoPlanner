package com.tubesApmob.doplanner

import android.os.Bundle
import com.tubesApmob.doplanner.databinding.DplTambahJadwalBinding


class TambahJadwalActivity : ActivityIntent() {
    private lateinit var binding: DplTambahJadwalBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DplTambahJadwalBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}