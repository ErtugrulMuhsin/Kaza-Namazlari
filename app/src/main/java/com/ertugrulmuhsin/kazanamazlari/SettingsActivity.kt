package com.ertugrulmuhsin.kazanamazlari

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(android.R.id.content, SettingsFragment())
                .commit()
        }
        // BAŞLIK ÇUBUĞU KODLARI BURADAN TAMAMEN SİLİNDİ
    }
}
