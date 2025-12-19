package com.example.services_app.activities

import android.content.ActivityNotFoundException
import android.graphics.Paint
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.bumptech.glide.Glide
import com.example.services_app.R
import com.example.services_app.databinding.ActivityDetailBinding
import com.example.services_app.domain.ItemModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DetailActivity : AppCompatActivity() {

    private var position: Int = 0
    private lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemModel

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var isFavourite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        bundle()
        checkIfFavourite()
    }

    private fun bundle() {
        binding.apply {
            item = intent.getSerializableExtra("object") as ItemModel
            position = intent.getIntExtra("position", 0)

            Glide.with(this@DetailActivity)
                .load(item.picUrl)
                .into(pic)

            pic.setBackgroundColor(getColor(backgrounds[position]))

            titleTxt.text = item.title ?: "Service"
            priceTxt.text = if (item.price != null) "$${item.price}" else "$0"
            oldPriceTxt.text = if (item.oldPrice != null) "$${item.oldPrice}" else "$0"
            offTxt.text = if (item.off != null) "%${item.off}\nOff" else "0%\nOff"

            // ✅ ViewBinding: price_1 → price1, oldprice_1 → oldprice1
            price1.text = if (item.classicPrice != null) "$${item.classicPrice}" else "$0"
            price2.text = if (item.premiumPrice != null) "$${item.premiumPrice}" else "$0"
            price3.text = if (item.platinumPrice != null) "$${item.platinumPrice}" else "$0"

            oldprice1.text = if (item.classicOldPrice != null) "$${item.classicOldPrice}" else "$0"
            oldprice2.text = if (item.premiumOldPrice != null) "$${item.premiumOldPrice}" else "$0"
            oldprice3.text = if (item.platinumOldPrice != null) "$${item.platinumOldPrice}" else "$0"

            // ✅ Strikethrough with TextView (not AppCompatTextView)
            setStrikethrough(oldPriceTxt, item.oldPrice != null)
            setStrikethrough(oldprice1, item.classicOldPrice != null)
            setStrikethrough(oldprice2, item.premiumOldPrice != null)
            setStrikethrough(oldprice3, item.platinumOldPrice != null)

            nameTxt.text = item.name ?: "Provider"
            jobTxt.text = item.job ?: "Service"
            aboutTxt.text = item.description ?: "No description."

            backBtn.setOnClickListener { finish() }

            Glide.with(this@DetailActivity)
                .load(item.profilePic)
                .into(profilePic)

            callBtn.setOnClickListener {
                item.phone?.let { dialNumber(this@DetailActivity, it) }
                    ?: Toast.makeText(this@DetailActivity, "Phone not available", Toast.LENGTH_SHORT).show()
            }

            messageBtn.setOnClickListener {
                item.phone?.let { sendSms(this@DetailActivity, it, "Hello! I'm interested in your services") }
                    ?: Toast.makeText(this@DetailActivity, "Phone not available", Toast.LENGTH_SHORT).show()
            }

            favBtn.setOnClickListener {
                toggleFavourite()
            }
            // ✅ Book Now Button — Firebase-ready data ke saath
            bookNowButton.setOnClickListener {
                val intent = Intent(this@DetailActivity, BookingActivity::class.java).apply {
                    putExtra("SERVICE_TITLE", item.title)
                    putExtra("SERVICE_PRICE", "$${item.price}")
                    putExtra("PROVIDER_PHONE", item.phone)
                    // Optional: description bhi bhej sakte hain
                    // putExtra("SERVICE_DESCRIPTION", item.description)
                }
                startActivity(intent)
            }
        }
    }

    // ✅ FIXED: Accepts TextView (not AppCompatTextView)
    private fun setStrikethrough(textView: TextView, enable: Boolean) {
        textView.paintFlags = if (enable) {
            textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    // === PHONE & SMS ===
    private fun dialNumber(context: Context, phoneNumber: String) {
        try {
            val uri = Uri.parse("tel:${Uri.encode(phoneNumber.trim())}")
            startActivity(Intent(Intent.ACTION_DIAL, uri))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Dialer app not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendSms(context: Context, phoneNumber: String, body: String) {
        try {
            val uri = Uri.parse("smsto:${Uri.encode(phoneNumber.trim())}")
            val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
                putExtra("sms_body", body)
            }
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "SMS app not found", Toast.LENGTH_SHORT).show()
        }
    }

    // === FAVOURITES (REALTIME DATABASE) ===
    private fun getFavKey(): String {
        return item.title?.replace(" ", "")?.lowercase()?.takeIf { it.isNotEmpty() }
            ?: "service_${System.currentTimeMillis()}"
    }

    private fun checkIfFavourite() {
        val userId = auth.currentUser?.uid ?: return
        database.getReference("favourites").child(userId).child(getFavKey())
            .get().addOnSuccessListener { snapshot ->
                isFavourite = snapshot.exists()
                updateFavIcon()
            }
    }

    private fun updateFavIcon() {
        if (isFavourite) {
            binding.favBtn.setColorFilter(getColor(android.R.color.black))
        } else {
            binding.favBtn.clearColorFilter()
        }
    }

    private fun toggleFavourite() {
        val user = auth.currentUser ?: return
        val userId = user.uid
        val key = getFavKey()
        val ref = database.getReference("favourites").child(userId).child(key)

        if (isFavourite) {
            ref.removeValue().addOnCompleteListener {
                isFavourite = false
                updateFavIcon()
                Toast.makeText(this, "Removed from favourites", Toast.LENGTH_SHORT).show()
            }
        } else {
            val data = mapOf(
                "title" to (item.title ?: ""),
                "subtitle" to (item.subtitle ?: ""),
                "description" to (item.description ?: ""),
                "picUrl" to (item.picUrl ?: ""),
                "profilePic" to (item.profilePic ?: ""),
                "price" to (item.price ?: 0),
                "oldPrice" to (item.oldPrice ?: 0),
                "off" to (item.off ?: 0),
                "classicPrice" to (item.classicPrice ?: 0),
                "classicOldPrice" to (item.classicOldPrice ?: 0),
                "premiumPrice" to (item.premiumPrice ?: 0),
                "premiumOldPrice" to (item.premiumOldPrice ?: 0),
                "platinumPrice" to (item.platinumPrice ?: 0),
                "platinumOldPrice" to (item.platinumOldPrice ?: 0),
                "name" to (item.name ?: ""),
                "job" to (item.job ?: ""),
                "categoryId" to (item.categoryId ?: ""),
                "phone" to (item.phone ?: ""),
                "showInAll" to item.showInAll
            )
            ref.setValue(data).addOnCompleteListener {
                if (it.isSuccessful) {
                    isFavourite = true
                    updateFavIcon()
                    Toast.makeText(this, "Service added to favourites!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to save favourite", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val backgrounds = listOf(
        R.color.pink,
        R.color.green,
        R.color.brown,
        R.color.blue
    )
}