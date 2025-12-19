// File: domain/BookingModel.kt
package com.example.services_app.domain

data class BookingModel(
    var service: String = "",
    var price: String = "",
    var user_name: String = "",
    var user_email: String = "",
    var user_phone: String = "",
    var address: String = "",
    var date: String = "",
    var time: String = "",
    var status: String = "Pending",
    var timestamp: Any? = null,
    var id: String? = null // Firebase key
)