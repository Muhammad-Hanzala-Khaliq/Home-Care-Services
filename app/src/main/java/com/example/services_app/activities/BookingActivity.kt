package com.example.services_app.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.services_app.R
import com.example.services_app.databinding.ActivityBookingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import java.util.*

class BookingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingBinding
    private lateinit var auth: FirebaseAuth
    private var selectedDate = ""
    private var selectedTime = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Status bar style
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightBrown)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        auth = FirebaseAuth.getInstance()

        // Intent se service data lena
        val serviceTitle = intent.getStringExtra("SERVICE_TITLE") ?: "Service"
        val servicePrice = intent.getStringExtra("SERVICE_PRICE") ?: "$0"
        val providerPhone = intent.getStringExtra("PROVIDER_PHONE") ?: ""

        // Firebase se user ka name aur email lena
        val currentUser = auth.currentUser
        val userName = currentUser?.displayName ?: currentUser?.email?.substringBefore("@") ?: "User"
        val userEmail = currentUser?.email ?: "—"

        // UI mein set karna
        binding.serviceName.text = serviceTitle
        binding.servicePrice.text = servicePrice
        binding.userName.text = userName
        binding.userEmail.text = userEmail
        binding.userPhone.text = providerPhone // Optional: provider ka phone user info mein dikhega

        // Date & Time Pickers
        binding.dateInput.setOnClickListener { showDatePicker() }
        binding.timeInput.setOnClickListener { showTimePicker() }

        // Back button
        binding.backBtn.setOnClickListener { onBackPressed() }

        // Confirm Booking
        binding.confirmBtn.setOnClickListener {
            confirmBooking(serviceTitle, servicePrice, providerPhone, userName, userEmail)
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, y, m, d ->
            selectedDate = "$d/${m + 1}/$y"
            binding.dateInput.setText(selectedDate)
        }, year, month, day).show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, h, m ->
            selectedTime = String.format("%02d:%02d", h, m)
            binding.timeInput.setText(selectedTime)
        }, hour, minute, true).show()
    }

    private fun confirmBooking(
        title: String,
        price: String,
        phone: String,
        userName: String,
        userEmail: String
    ) {
        val address = binding.addressInput.text.toString().trim()

        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedTime.isEmpty()) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show()
            return
        }
        if (address.isEmpty()) {
            Toast.makeText(this, "Please enter your address", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid ?: return
        val bookingId = FirebaseDatabase.getInstance().reference.push().key ?: return

        // 🔥 Poora booking data — user info + service info
        val booking = hashMapOf(
            "service" to title,
            "price" to price,
            "user_name" to userName,
            "user_email" to userEmail,
            "user_phone" to phone, // provider ka phone
            "address" to address,
            "date" to selectedDate,
            "time" to selectedTime,
            "timestamp" to ServerValue.TIMESTAMP,
            "status" to "Pending"
        )

        FirebaseDatabase.getInstance()
            .getReference("bookings")
            .child(userId)
            .child(bookingId)
            .setValue(booking)
            .addOnSuccessListener {
                Toast.makeText(this, "✅ Booking Confirmed!", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "❌ Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}