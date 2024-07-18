package com.example.banktransactions.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.banktransactions.R
import com.example.banktransactions.adapters.TransactionAdapter
import com.example.banktransactions.data.daos.DatabaseHelper
import com.example.banktransactions.data.entities.Transaction
import com.example.banktransactions.databinding.ActivityMainBinding
import com.example.banktransactions.utils.TransactionType
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TransactionAdapter
    private lateinit var createTransactionLauncher: ActivityResultLauncher<Intent>
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        setupRecyclerView()
        setupAddTransactionButton()

        createTransactionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val amount = data?.getDoubleExtra("AMOUNT", 0.0)
                val description = data?.getStringExtra("DESCRIPTION")
                val dateString = data?.getStringExtra("DATE")
                val date = SimpleDateFormat("yyyy-MM-dd").parse(dateString!!)
                val type = data?.getSerializableExtra("TYPE") as? TransactionType
                if (amount != null && description != null && date != null && type != null) {
                    val transaction = Transaction(
                        id = 0,
                        amount = amount,
                        description = description,
                        date = date,
                        type = type
                    )
                    addTransaction(transaction)
                }
            }
        }

        loadTransactions()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                startCreateTransactionActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TransactionAdapter(emptyList()) { id ->
            deleteTransaction(id)
        }
        binding.recyclerView.adapter = adapter
    }

    private fun setupAddTransactionButton() {
        binding.btnAddTransaction.setOnClickListener {
            startCreateTransactionActivity()
        }
    }

    private fun startCreateTransactionActivity() {
        val intent = Intent(this, CreateTransactionActivity::class.java)
        createTransactionLauncher.launch(intent)
    }

    private fun addTransaction(transaction: Transaction) {
        val id = dbHelper.addTransaction(transaction)
        loadTransactions()
    }

    private fun deleteTransaction(transactionId: Long) {
        dbHelper.deleteTransaction(transactionId)
        loadTransactions()
    }

    private fun loadTransactions() {
        val transactions = dbHelper.getAllTransactions()
        adapter.updateData(transactions)
        updateBalance(transactions)
    }

    private fun updateBalance(transactions: List<Transaction>) {
        val balance = transactions.sumOf { if (it.type == TransactionType.INCOME) it.amount else -it.amount }
        binding.tvBalance.text = "Balance: $%.2f".format(balance)
    }
}
