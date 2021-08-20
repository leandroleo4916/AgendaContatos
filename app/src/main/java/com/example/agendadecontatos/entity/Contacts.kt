package com.example.agendadecontatos.entity

data class Contacts (
    val id: Long,
    val lookupKey: String,
    val name: String,
    val phone: String
)