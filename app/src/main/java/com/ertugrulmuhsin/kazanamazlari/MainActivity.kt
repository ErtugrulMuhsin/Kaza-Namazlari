package com.ertugrulmuhsin.kazanamazlari

import android.content.Intent
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
    }

    private fun showKazaDialog() {
        val vakitler = arrayOf("Sabah", "Öğle", "İkindi", "Akşam", "Yatsı")
        val checkedItems = BooleanArray(vakitler.size) { i ->
            db.isKazaExists(selectedDate, vakitler[i])
        }

        val builder = AlertDialog.Builder(this)
        // GÜNCELLENDİ: Başlık artık strings.xml'den geliyor.
        builder.setTitle("$selectedDate ${getString(R.string.dialog_title_prefix)}")
        builder.setMultiChoiceItems(vakitler, checkedItems) { _, which, isChecked ->
            if (isChecked) {
                db.addKaza(selectedDate, vakitler[which])
            } else {
                db.deleteKaza(selectedDate, vakitler[which])
            }
        }

        // GÜNCELLENDİ: Buton metni strings.xml'den geliyor.
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
            R.id.action_about -> {
                showAboutDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAboutDialog() {
        // GÜNCELLENDİ: Tüm metinler strings.xml'den geliyor.
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.about_app_title))
            .setMessage(getString(R.string.about_app_message))
            .setPositiveButton(getString(R.string.ok_button), null)
            .show()
    }
}