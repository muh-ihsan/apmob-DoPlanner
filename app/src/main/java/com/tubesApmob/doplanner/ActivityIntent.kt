package com.tubesApmob.doplanner

import android.content.Context
import android.content.Intent
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity

open class ActivityIntent : AppCompatActivity() {
    // fungsi untuk memulai activity menu utama
    fun keActivityMain(packageContext: Context) {
        val intentMain = Intent(packageContext, MainActivity::class.java)
        startActivity(intentMain)
    }

    // fungsi untuk memulai activity login
    fun keActivityLogin(packageContext: Context) {
        val intentLogin = Intent(packageContext, LoginActivity::class.java)
        startActivity(intentLogin)
    }

    // fungsi untuk memulai activity signup
    fun keActivitySignup(packageContext: Context) {
        val intentSignup = Intent(packageContext, SignupActivity::class.java)
        startActivity(intentSignup)
    }

    // fungsi untuk memulai activity lupa password
    fun keActivityLupaPass(packageContext: Context) {
        val intentForgot = Intent(packageContext, ForgotPasswordActivity::class.java)
        startActivity(intentForgot)
    }

    // fungsi untuk memulai activity setup pertama
    fun keActivityFirstSetup(packageContext: Context) {
        val intentSetup = Intent(packageContext, FirstSetupActivity::class.java)
        startActivity(intentSetup)
    }

    // fungsi untuk memulai activity jadwal
    fun keActivityJadwal(packageContext: Context) {
        val intentJadwal = Intent(packageContext, JadwalActivity::class.java)
        startActivity(intentJadwal)
    }

    // fungsi untuk memulai activity tugas
    fun keActivityTugas(packageContext: Context) {
        val intentTugas = Intent(packageContext, TugasActivity::class.java)
        startActivity(intentTugas)
    }

    // fungsi untuk memulai activity daftar mahasiswa
    fun keActivityMahasiswa(packageContext: Context) {
        val intentMahasiswa = Intent(packageContext, DaftarMahasiswaActivity::class.java)
        startActivity(intentMahasiswa)
    }

    // fungsi untuk memulai activity tambah jadwal
    fun keActivityTambahJadwal(packageContext: Context) {
        val intentTambahJadwal = Intent(packageContext, TambahJadwalActivity::class.java)
        startActivity(intentTambahJadwal)
    }

    // fungsi untuk memulai activity tambah tugas
    fun keActivityTambahTugas(packageContext: Context) {
        val intentTambahTugas = Intent(packageContext, TambahTugasActivity::class.java)
        startActivity(intentTambahTugas)
    }

    // fungsi untuk memulai activity tambah mahasiswa
    fun keActivityTambahMahasiswa(packageContext: Context) {
        val intentTambahMahasiswa = Intent(packageContext, TambahMahasiswaActivity::class.java)
        startActivity(intentTambahMahasiswa)
    }

    // fungsi untuk memulai activity detail tugas
    fun keActivityDetailTugas(packageContext: Context) {
        val intentDetailTugas = Intent(packageContext, DetailTugasActivity::class.java)
        startActivity(intentDetailTugas)
    }
}