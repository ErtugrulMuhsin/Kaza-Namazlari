package com.ertugrulmuhsin.kazanamazlari

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "KazaNamazlariDB"
        private const val TABLE_KAZA = "kaza_namazlari"
        private const val KEY_ID = "id"
        private const val KEY_TARIH = "tarih"
        private const val KEY_VAKIT = "vakit"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE $TABLE_KAZA($KEY_ID INTEGER PRIMARY KEY, $KEY_TARIH TEXT, $KEY_VAKIT TEXT)")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_KAZA")
        onCreate(db)
    }

    fun addKaza(tarih: String, vakit: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_TARIH, tarih)
        values.put(KEY_VAKIT, vakit)
        db.insert(TABLE_KAZA, null, values)
        db.close()
    }

    fun deleteKaza(tarih: String, vakit: String) {
        val db = this.writableDatabase
        db.delete(TABLE_KAZA, "$KEY_TARIH = ? AND $KEY_VAKIT = ?", arrayOf(tarih, vakit))
        db.close()
    }

    fun isKazaExists(tarih: String, vakit: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT 1 FROM $TABLE_KAZA WHERE $KEY_TARIH = ? AND $KEY_VAKIT = ?", arrayOf(tarih, vakit))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun getAllKaza(): List<Pair<String, String>> {
        val kazaList = ArrayList<Pair<String, String>>()
        val selectQuery = "SELECT * FROM $TABLE_KAZA ORDER BY $KEY_TARIH DESC"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val tarihIndex = cursor.getColumnIndex(KEY_TARIH)
                    val vakitIndex = cursor.getColumnIndex(KEY_VAKIT)

                    if (tarihIndex != -1 && vakitIndex != -1) {
                        kazaList.add(Pair(cursor.getString(tarihIndex), cursor.getString(vakitIndex)))
                    }
                } while (cursor.moveToNext())
            }
        } finally {
            cursor?.close()
            db.close()
        }
        return kazaList
    }
}
