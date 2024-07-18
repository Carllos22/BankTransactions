package com.example.banktransactions.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.banktransactions.databinding.ActivityCreateTransactionBinding
import com.example.banktransactions.utils.TransactionType
import java.text.SimpleDateFormat
import java.util.*

class CreateTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnAdd.setOnClickListener {
            saveTransaction()
        }
    }

    private fun saveTransaction() {
        val amount = binding.etAmount.text.toString().toDoubleOrNull() ?: 0.0
        val description = binding.etDescription.text.toString()
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val type = if (amount >= 0) TransactionType.INCOME else TransactionType.EXPENSE

        val intent = Intent()
        intent.putExtra("AMOUNT", amount)
        intent.putExtra("DESCRIPTION", description)
        intent.putExtra("DATE", date)
        intent.putExtra("TYPE", type)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
