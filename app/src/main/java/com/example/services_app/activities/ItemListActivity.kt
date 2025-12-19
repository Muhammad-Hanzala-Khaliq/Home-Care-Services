package com.example.services_app.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.services_app.R
import com.example.services_app.adapters.ItemListCategoryAdapter
import com.example.services_app.databinding.ActivityItemListBinding
import com.example.services_app.viewModel.MainViewModel

class ItemListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityItemListBinding
    private val viewModel: MainViewModel by lazy{
        ViewModelProvider(this)[MainViewModel::class.java]
    }
    private var id: String= ""
    private var title:String=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityItemListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor= ContextCompat.getColor(this,R.color.lightBrown)
        val decor=window.decorView
        decor.systemUiVisibility= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
// Cart button par click → FavouritesActivity kholo
        binding.cartBtn.setOnClickListener {
            startActivity(Intent(this, FavouritesActivity::class.java))
        }
        getBundles()
        initList()
    }

    private fun initList() {
        binding.apply {
            progressBar.visibility= View.VISIBLE
            viewModel.loadItems(id).observe(this@ItemListActivity, Observer{
                view.layoutManager= LinearLayoutManager(this@ItemListActivity, LinearLayoutManager.VERTICAL,false)
                view.adapter= ItemListCategoryAdapter(it)
                progressBar.visibility= View.GONE
            })
            backBtn.setOnClickListener { finish() }

        }
    }

    private fun getBundles() {
        id=intent.getStringExtra("id")!!
        title=intent.getStringExtra("title")!!

        binding.titleTxt.text=title
    }
}