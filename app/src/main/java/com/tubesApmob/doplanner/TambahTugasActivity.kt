package com.tubesApmob.doplanner

import android.os.Bundle
import com.tubesApmob.doplanner.databinding.DplTambahTugasBinding

class TambahTugasActivity : ActivityIntent() {
    private lateinit var binding: DplTambahTugasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DplTambahTugasBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}