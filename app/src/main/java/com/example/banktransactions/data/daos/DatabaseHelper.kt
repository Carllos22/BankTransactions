package com.example.banktransactions.data.daos

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.banktransactions.data.entities.Transaction
import java.text.SimpleDateFormat
import java.util.*

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {

        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "transactions.db"
        private const val TABLE_NAME = "transactions"
        private const val COLUMN_ID = "id"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_DESCRIPTION = "description"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableSQL = (
                "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_AMOUNT REAL, "
                + "$COLUMN_DATE TEXT, "
                + "$COLUMN_DESCRIPTION TEXT)")
        db?.execSQL(createTableSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addTransaction(transaction: Transaction): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_AMOUNT, transaction.amount)
        contentValues.put(COLUMN_DATE, SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
            Locale.getDefault()).format(transaction.date))
        contentValues.put(COLUMN_DESCRIPTION, transaction.description)
        val id = db.insert(TABLE_NAME, null, contentValues)
        db.close()
        return id
    }


    fun getAllTransactions(): List<Transaction> {
        val transactionList = mutableListOf<Transaction>()
        val selectQuery = "SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_DATE DESC"
        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery(selectQuery, null)
        cursor?.use {
            while (it.moveToNext()) {
                val idIndex = it.getColumnIndex(COLUMN_ID)
                val amountIndex = it.getColumnIndex(COLUMN_AMOUNT)
                val dateIndex = it.getColumnIndex(COLUMN_DATE)
                val descriptionIndex = it.getColumnIndex(COLUMN_DESCRIPTION)

                if (idIndex != -1 && amountIndex != -1 && dateIndex != -1 && descriptionIndex != -1) {
                    val id = it.getLong(idIndex)
                    val amount = it.getDouble(amountIndex)
                    val dateString = it.getString(dateIndex)
                    val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault()).parse(dateString)
                    val description = it.getString(descriptionIndex)
                    val transaction = Transaction(id, amount, date, description)
                    transactionList.add(transaction)
                } else {

                }
            }
        }
        db.close()
        return transactionList
    }


    fun deleteTransaction(transactionId: Long) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(transactionId.toString()))
        db.close()
    }
}