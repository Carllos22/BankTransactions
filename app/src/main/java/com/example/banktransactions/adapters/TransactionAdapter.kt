package com.example.banktransactions.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.banktransactions.data.entities.Transaction
import com.example.banktransactions.databinding.ItemTransactionBinding
import com.example.banktransactions.utils.TransactionType
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(
    private var transactionList: List<Transaction>,
    private val onDeleteClick: (Long) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        holder.bind(transaction)
        holder.binding.deleteButton.setOnClickListener {
            onDeleteClick(transaction.id)
        }
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    fun updateData(newTransactionList: List<Transaction>) {
        transactionList = newTransactionList
        notifyDataSetChanged()
    }

    inner class TransactionViewHolder(val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: Transaction) {
            binding.tvAmount.text = transaction.amount.toString()
            binding.tvDate.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(transaction.date)
            binding.tvDescription.text = transaction.description

            if (transaction.type == TransactionType.INCOME) {
                binding.tvType.text = "Income"
                binding.tvType.setTextColor(Color.GREEN)
                binding.tvAmount.setTextColor(Color.GREEN)
            } else {
                binding.tvType.text = "Expense"
                binding.tvType.setTextColor(Color.RED)
                binding.tvAmount.setTextColor(Color.RED)
            }
        }
    }
}

