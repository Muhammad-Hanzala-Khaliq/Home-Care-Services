// File: adapters/BookingsAdapter.kt
package com.example.services_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.services_app.R
import com.example.services_app.domain.BookingModel


class BookingsAdapter(
    private val bookings: List<BookingModel>,
    private val onActionClick: (BookingModel, String) -> Unit
) : RecyclerView.Adapter<BookingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val booking = bookings[position]
        holder.bind(booking, onActionClick)
    }

    override fun getItemCount() = bookings.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val serviceName = itemView.findViewById<TextView>(R.id.serviceName)
        private val priceText = itemView.findViewById<TextView>(R.id.priceText)
        private val dateText = itemView.findViewById<TextView>(R.id.dateText)
        private val statusText = itemView.findViewById<TextView>(R.id.statusText)
        private val cancelBtn = itemView.findViewById<TextView>(R.id.cancelBtn)
        private val deleteBtn = itemView.findViewById<TextView>(R.id.deleteBtn)

        fun bind(booking: BookingModel, onAction: (BookingModel, String) -> Unit) {
            serviceName.text = booking.service
            priceText.text = booking.price
            dateText.text = "${booking.date} at ${booking.time}"

            // ✅ Handle all 3 statuses — as per your requirement
            when (booking.status) {
                "Cancelled" -> {
                    statusText.text = "Cancelled"
                    statusText.setBackgroundResource(R.drawable.status_badge_cancelled)
                    cancelBtn.visibility = View.GONE
                    deleteBtn.visibility = View.VISIBLE
                }
                "Completed" -> {
                    statusText.text = "Completed"
                    statusText.setBackgroundResource(R.drawable.status_badge_completed)
                    cancelBtn.visibility = View.GONE
                    deleteBtn.visibility = View.VISIBLE // ✅ Delete allowed for cleanup
                }
                else -> { // Pending
                    statusText.text = "Pending"
                    statusText.setBackgroundResource(R.drawable.status_badge_bg)
                    cancelBtn.visibility = View.VISIBLE
                    deleteBtn.visibility = View.VISIBLE
                    cancelBtn.setOnClickListener { onAction(booking, "cancel") }
                }
            }

            // White text on colored badge
            statusText.setTextColor(itemView.context.getColor(android.R.color.white))
            statusText.textSize = 12f

            // Set Delete click listener if visible
            if (deleteBtn.visibility == View.VISIBLE) {
                deleteBtn.setOnClickListener { onAction(booking, "delete") }
            }
        }
    }
}