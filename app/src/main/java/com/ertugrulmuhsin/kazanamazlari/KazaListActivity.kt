package com.ertugrulmuhsin.kazanamazlari

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class KazaListActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var kazaListView: ListView
    private lateinit var kazaList: List<Pair<String, String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kaza_list)

        // --- TEMİZLİK ---
        // Üst bar (ActionBar) ile ilgili tüm kodlar buradan kaldırıldı.
        // Artık başlık, tasarım dosyasının (XML) kendisi tarafından yönetiliyor.

        db = DatabaseHelper(this)
        kazaListView = findViewById(R.id.kazaListView)

        kazaListView.setOnItemClickListener { parent, view, position, id ->
            if (kazaList.isEmpty()) {
                return@setOnItemClickListener
            }

            val selectedKaza = kazaList[position]
            val tarih = selectedKaza.first
            val vakit = selectedKaza.second
            // `R.string.namaz_suffix` yerine doğrudan metin kullanıldı, uyumluluk için.
            val selectedItemText = "$tarih - $vakit Namazı"

            showDeleteConfirmationDialog(tarih, vakit, selectedItemText)
        }
    }

    override fun onResume() {
        super.onResume()
        loadKazaNamazlari()
    }

    private fun loadKazaNamazlari() {
        kazaList = db.getAllKaza()

        if (kazaList.isEmpty()) {
            // `R.string.no_kazas_to_show` yerine doğrudan metin kullanıldı, uyumluluk için.
            val emptyList = listOf("Takip edilecek kaza namazı bulunmuyor.")
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emptyList)
            kazaListView.adapter = adapter
        } else {
            val formattedList = kazaList.map { "${it.first} - ${it.second} Namazı" }
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, formattedList)
            kazaListView.adapter = adapter
        }
    }

    private fun showDeleteConfirmationDialog(tarih: String, vakit: String, itemText: String) {
        AlertDialog.Builder(this)
             // `R.string` yerine doğrudan metinler kullanıldı, uyumluluk için.
            .setTitle("İşlem Seçin")
            .setMessage("'$itemText'\n\nBu kaza namazını kıldıysanız listeden silebilirsiniz.")
            .setPositiveButton("Kaza Namazımı Kıldım") { _, _ ->
                db.deleteKaza(tarih, vakit)
                Toast.makeText(this, "Namaz kazası silindi.", Toast.LENGTH_SHORT).show()
                loadKazaNamazlari()
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    // --- TEMİZLİK ---
    // `onSupportNavigateUp` fonksiyonu, artık bir geri oku olmadığı için buradan tamamen kaldırıldı.
    // Telefonun kendi geri tuşu hala çalışmaya devam edecektir.
}
