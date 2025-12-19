package com.example.services_app.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.services_app.R
import com.example.services_app.adapters.BookingsAdapter
import com.example.services_app.databinding.ActivityMyBookingsBinding
import com.example.services_app.domain.BookingModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyBookingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyBookingsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private val bookingsList = mutableListOf<BookingModel>()
    private lateinit var adapter: BookingsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyBookingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Status bar
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightBrown)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        // 🔙 Back button
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        setupRecyclerView()
        loadBookings()
    }

    private fun setupRecyclerView() {
        adapter = BookingsAdapter(bookingsList) { booking, action ->
            when (action) {
                "cancel" -> cancelBooking(booking)
                "delete" -> deleteBooking(booking)
            }
        }
        binding.bookingsList.layoutManager = LinearLayoutManager(this)
        binding.bookingsList.adapter = adapter
    }

    private fun loadBookings() {
        val userId = auth.currentUser?.uid ?: return
        binding.progressBar.visibility = View.VISIBLE

        database.getReference("bookings").child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    bookingsList.clear()
                    for (child in snapshot.children) {
                        val booking = child.getValue(BookingModel::class.java)
                        if (booking != null) {
                            booking.id = child.key // important for delete/cancel
                            bookingsList.add(booking)
                        }
                    }
                    adapter.notifyDataSetChanged()
                    binding.progressBar.visibility = View.GONE
                    if (bookingsList.isEmpty()) {
                        binding.emptyText.visibility = View.VISIBLE
                    } else {
                        binding.emptyText.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@MyBookingsActivity, "Failed to load bookings", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun cancelBooking(booking: BookingModel) {
        val userId = auth.currentUser?.uid ?: return
        val bookingId = booking.id ?: return

        // Status "Cancelled" karein
        database.getReference("bookings").child(userId).child(bookingId)
            .child("status").setValue("Cancelled")
            .addOnSuccessListener {
                Toast.makeText(this, "Booking cancelled", Toast.LENGTH_SHORT).show()
                loadBookings() // Refresh list
            }
    }

    private fun deleteBooking(booking: BookingModel) {
        val userId = auth.currentUser?.uid ?: return
        val bookingId = booking.id ?: return

        // Purani booking delete karein
        database.getReference("bookings").child(userId).child(bookingId)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Booking removed", Toast.LENGTH_SHORT).show()
                loadBookings()
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}