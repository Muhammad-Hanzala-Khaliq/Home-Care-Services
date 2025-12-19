package com.example.services_app.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.services_app.R
import com.example.services_app.adapters.CategoryAdapters
import com.example.services_app.databinding.ActivityMainBinding
import com.example.services_app.viewModel.MainViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: MainViewModel
    private val categoryAdapter = CategoryAdapters(mutableListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // 🔒 Redirect to login if not authenticated
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // 🔙 Optional: Modern back press handling (remove if not needed)
        // onBackPressedDispatcher.addCallback(this) {
        //     super.onBackPressed()
        // }

        setupUi()
        setupViewModel()
        loadContent()
    }

    private fun setupUi() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightBrown)
        val decor = window.decorView
        decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        binding.categoryList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.categoryList.adapter = categoryAdapter

        // 🔻 PROFILE BUTTON — YEH NAEE LINE HAI!
        // ✅ YEH SAHI TAREEQA HAI — ProfileActivity khol dega
        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        binding.cartBtn.setOnClickListener {
            startActivity(Intent(this, FavouritesActivity::class.java))
        }
        binding.bookmarkBtn.setOnClickListener {
            startActivity(Intent(this, MyBookingsActivity::class.java))
        }
    }


    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    private fun loadContent() {
        // Load Banner
        viewModel.loadBanner()
        viewModel.banner.observe(this) { data ->
            if (data.isNotEmpty()) {
                Glide.with(this).load(data[0].url).into(binding.banner)
            }
        }

        // ✅ FIXED: Safe handling for nullable String
        viewModel.loadProfile()
        viewModel.profile.observe(this) { profile ->
            binding.nameTxt.text = profile.name.ifEmpty { "User" }

            val picUrl = profile.profilePic
                ?: "https://res.cloudinary.com/dkikc5ywq/image/upload/v1758824328/profile_j8dkft.jpg"

            Glide.with(this).load(picUrl).into(binding.profilePic)
        }

        // Load Categories
        binding.progressBarCategory.visibility = View.VISIBLE
        viewModel.loadCategories()
        viewModel.category.observe(this) { categories ->
            binding.progressBarCategory.visibility = View.GONE
            categoryAdapter.updateData(categories)
        }
    }

    // ✅ Keep this if you don't use onBackPressedDispatcher
    override fun onBackPressed() {
        super.onBackPressed()
    }
}