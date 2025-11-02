package com.ertugrulmuhsin.kazanamazlari

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class KazaListActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var kazaListView: ListView
    private lateinit var kazaList: List<Pair<String, String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kaza_list)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Kaza Listesi"

        db = DatabaseHelper(this)
        kazaListView = findViewById(R.id.kazaListView)

        // YENİ ÖZELLİK: Listedeki bir öğeye tıklandığında...
        kazaListView.setOnItemClickListener { parent, view, position, id ->
            // Eğer liste boşsa ve "namaz bulunmuyor" yazısına tıklandıysa bir şey yapma.
            if (kazaList.isEmpty()) {
                return@setOnItemClickListener
            }

            // Tıklanan namazın bilgilerini al.
            val selectedKaza = kazaList[position]
            val tarih = selectedKaza.first
            val vakit = selectedKaza.second
            val selectedItemText = "$tarih - $vakit ${getString(R.string.namaz_suffix)}"

            // Onay penceresi göster.
            showDeleteConfirmationDialog(tarih, vakit, selectedItemText)
        }
    }

    override fun onResume() {
        super.onResume()
        // Ekran her açıldığında listeyi yenile.
        loadKazaNamazlari()
    }

    private fun loadKazaNamazlari() {
        kazaList = db.getAllKaza()

        if (kazaList.isEmpty()) {
            val emptyList = listOf(getString(R.string.no_kazas_to_show))
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emptyList)
            kazaListView.adapter = adapter
        } else {
            val formattedList = kazaList.map { "${it.first} - ${it.second} ${getString(R.string.namaz_suffix)}" }
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, formattedList)
            kazaListView.adapter = adapter
        }
    }

    // YENİ FONKSİYON: Silme onayı penceresi.
    private fun showDeleteConfirmationDialog(tarih: String, vakit: String, itemText: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_action_title))
            .setMessage("'$itemText'\n\nBu kaza namazını kıldıysanız listeden silebilirsiniz.")
            .setPositiveButton(getString(R.string.delete_kaza_button)) { _, _ ->
                // "Kıldım" butonuna basılınca...
                db.deleteKaza(tarih, vakit) // Veritabanından sil.
                Toast.makeText(this, getString(R.string.kaza_deleted_toast), Toast.LENGTH_SHORT).show() // Bilgi mesajı göster.
                loadKazaNamazlari() // Listeyi yenile.
            }
            .setNegativeButton(getString(R.string.cancel_button), null) // "İptal" butonu bir şey yapmaz.
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
