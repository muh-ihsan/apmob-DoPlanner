package com.tubesApmob.doplanner

import android.os.Bundle
import android.widget.ArrayAdapter
import com.tubesApmob.doplanner.databinding.DplFirstTimeSetupBinding

class FirstSetupActivity : ActivityIntent() {
    private lateinit var binding: DplFirstTimeSetupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DplFirstTimeSetupBinding.inflate(layoutInflater)

        val menuIsijurusan = binding.menuIsijurusanSetup

        val items = listOf("Teknik Komputer", "Teknik Telekomunikasi", "Teknik Elektro", "Teknik Fisika")
        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        menuIsijurusan.setAdapter(adapter)

        setContentView(binding.root)
    }
}