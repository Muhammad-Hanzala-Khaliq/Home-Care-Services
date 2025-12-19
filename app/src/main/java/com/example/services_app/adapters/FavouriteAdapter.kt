package com.example.services_app.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.services_app.R
import com.example.services_app.domain.ItemModel
import com.google.android.material.card.MaterialCardView

class FavouriteAdapter(
    private val items: MutableList<ItemModel>,
    private val onRemoveClick: (ItemModel) -> Unit
) : RecyclerView.Adapter<FavouriteAdapter.ViewHolder>() {

    // Define your background colors (same as in ItemListActivity)
    private val backgrounds = listOf(
        R.color.pink,
        R.color.green,
        R.color.brown,
        R.color.blue
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favourite_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position, onRemoveClick)
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: MaterialCardView = itemView.findViewById(R.id.cardView)
        private val titleTxt: TextView = itemView.findViewById(R.id.titleTxt)
        private val priceTxt: TextView = itemView.findViewById(R.id.priceTxt)
        private val nameTxt: TextView = itemView.findViewById(R.id.nameTxt)
        private val jobTxt: TextView = itemView.findViewById(R.id.jobTxt)
        private val pic = itemView.findViewById<android.widget.ImageView>(R.id.pic)
        private val profilePic = itemView.findViewById<android.widget.ImageView>(R.id.profilePic)
        private val removeBtn: Button = itemView.findViewById(R.id.removeFavBtn)

        fun bind(item: ItemModel, position: Int, onRemove: (ItemModel) -> Unit) {
            // ✅ Set alternating background color
            val colorRes = backgrounds[position % backgrounds.size]
            val color = ContextCompat.getColor(itemView.context, colorRes)
            cardView.setCardBackgroundColor(color)

            titleTxt.text = item.title ?: "Service"
            priceTxt.text = item.price?.let { "$$$it" } ?: "$0"
            nameTxt.text = item.name ?: "Provider"
            jobTxt.text = item.job ?: "Service"

            Glide.with(itemView.context)
                .load(item.picUrl)
                .apply(RequestOptions.centerCropTransform())
                .into(pic)

            Glide.with(itemView.context)
                .load(item.profilePic)
                .apply(RequestOptions.circleCropTransform())
                .into(profilePic)

            removeBtn.setOnClickListener {
                onRemove(item)
            }
        }
    }
}