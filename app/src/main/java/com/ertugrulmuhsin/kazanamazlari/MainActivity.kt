package com.ertugrulmuhsin.kazanamazlari

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.CalendarView
import androidx.appcompat.app.AlertDialog
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var db: DatabaseHelper
    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DatabaseHelper(this)
        calendarView = findViewById(R.id.calendarView)
        val btnShowKazaList: Button = findViewById(R.id.btnShowKazaList)

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        selectedDate = sdf.format(Date(calendarView.date))

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance()
            cal.set(year, month, dayOfMonth)
            selectedDate = sdf.format(cal.time)
            showKazaDialog()
        }

        btnShowKazaList.setOnClickListener {
            val intent = Intent(this, KazaListActivity::class.java)
            startActivity(intent)
        }

        // YENİ EKLENEN BÖLÜM: BİLDİRİM İZNİ VE İLK ZAMANLAMA
        requestNotificationPermission()
        NotificationScheduler.scheduleNotification(this)
    } // onCreate fonksiyonunun kapanışı

    // --- onCreate fonksiyonundan sonraki diğer fonksiyonlar ---

    private fun showKazaDialog() {
        val vakitler = arrayOf("Sabah", "Öğle", "İkindi", "Akşam", "Yatsı")
        val checkedItems = BooleanArray(vakitler.size) { i ->
            db.isKazaExists(selectedDate, vakitler[i])
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("$selectedDate ${getString(R.string.dialog_title_prefix)}")
        builder.setMultiChoiceItems(vakitler, checkedItems) { _, which, isChecked ->
            if (isChecked) {
                db.addKaza(selectedDate, vakitler[which])
            } else {
                db.deleteKaza(selectedDate, vakitler[which])
            }
        }

        builder.setPositiveButton(getString(R.string.save_and_close_button)) { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.action_about -> {
                showAboutDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.about_app_title))
            .setMessage(getString(R.string.about_app_message))
            .setPositiveButton(getString(R.string.ok_button), null)
            .show()
    }

    // YENİ EKLENEN FONKSİYON: BİLDİRİM İZNİ İSTEME
    private fun requestNotificationPermission() {
        // Sadece Android 13 (API 33, kod adı TIRAMISU) ve üstü için bu kontrolü yap.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Eğer bildirim gönderme iznimiz henüz verilmemişse...
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Kullanıcıya izin isteme diyalogunu göster.
                // 101, bu isteği tanımak için kullandığımız bir kod numarasıdır.
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }
}