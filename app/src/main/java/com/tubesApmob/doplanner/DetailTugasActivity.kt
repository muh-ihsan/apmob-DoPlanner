package com.tubesApmob.doplanner

import android.os.Bundle
import com.tubesApmob.doplanner.databinding.DplTugasDetailBinding

class DetailTugasActivity : BaseActivity() {
    private lateinit var binding: DplTugasDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DplTugasDetailBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}