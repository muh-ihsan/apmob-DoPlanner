package com.tubesApmob.doplanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dpl_login)

        val fragmentLogin = LoginFragment() // Deklarasi fragment login

        // Menampilkan fragment login email
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_login_flow, fragmentLogin)
            commit()
        }
    }
}