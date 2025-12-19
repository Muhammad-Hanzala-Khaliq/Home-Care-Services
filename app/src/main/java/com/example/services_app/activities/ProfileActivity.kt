package com.example.services_app.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.services_app.R
import com.example.services_app.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Status bar color set karein (aap ki theme ke mutabiq)
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightBrown)
        val decor = window.decorView
        decor.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        auth = FirebaseAuth.getInstance()

        // 🔙 BACK BUTTON — wapas jaane ke liye
        binding.backBtn.setOnClickListener {
            onBackPressed() // Ya phir: finish()
        }

        // 🔻 LOGOUT BUTTON — exactly aap ke MainActivity jaisa
        binding.btnLogoutProfile.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        loadUserProfile()
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid ?: return
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("name").getValue(String::class.java) ?: "Mohsen Jamali"
                    val email = snapshot.child("email").getValue(String::class.java) ?: "—"
                    val profilePic = snapshot.child("profilePic").getValue(String::class.java)
                        ?: "https://res.cloudinary.com/dkikc5ywq/image/upload/v1758824328/profile_j8dkft.jpg"

                    binding.profileName.text = name
                    binding.profileEmail.text = email
                    Glide.with(this@ProfileActivity)
                        .load(profilePic.trim())
                        .circleCrop()
                        .into(binding.profileImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Optional: Toast show kar sakte hain agar error ho
            }
        })
    }
}