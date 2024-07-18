package com.example.banktransactions.data.entities

import com.example.banktransactions.utils.TransactionType
import java.util.Date

data class Transaction(
    val id: Long,
    val amount: Double,
    val date: Date,
    val description: String,
    val type: TransactionType
)
