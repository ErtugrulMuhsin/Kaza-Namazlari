package com.ertugrulmuhsin.kazanamazlari

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // --- DEĞİŞİKLİK 1 ---
        // Kendi layout dosyamızı kullanmayı bıraktık.
        // Bu satırın silinmesi, Android'in varsayılan, akıllı düzenini kullanmasını sağlar.
        // setContentView(R.layout.settings_activity) // BU SATIR SİLİNDİ!

        // Eğer ekran ilk defa oluşturuluyorsa (döndürme gibi bir durum yoksa),
        // SettingsFragment'ı ekranın içine yerleştir.
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                // --- DEĞİŞİKLİK 2 ---
                // Fragment'ı yerleştireceğimiz yer olarak, bizim oluşturduğumuz özel ID yerine,
                // Android'in tüm Activity'lerde bulunan standart içerik alanını ('content') kullanıyoruz.
                .replace(android.R.id.content, SettingsFragment())
                .commit()
        }
        
        // Başlık çubuğuna bir "geri" oku ekle.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // Geri okuna basıldığında ne olacağını belirler.
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed() // Bir önceki ekrana dön.
        return true
    }
}
