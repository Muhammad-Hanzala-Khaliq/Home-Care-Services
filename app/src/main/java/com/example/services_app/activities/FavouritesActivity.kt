package com.example.services_app.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.services_app.adapters.FavouriteAdapter
import com.example.services_app.databinding.ActivityFavouritesBinding
import com.example.services_app.domain.ItemModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FavouritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavouritesBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private val favouriteList = mutableListOf<ItemModel>()
    private lateinit var adapter: FavouriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        setupUI()
        loadFavourites()
    }

    private fun setupUI() {
        // Back button
        binding.backBtn.setOnClickListener { finish() }

        // RecyclerView
        adapter = FavouriteAdapter(favouriteList) { item ->
            // Remove item
            removeFromFavourites(item)
        }
        binding.favRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.favRecyclerView.adapter = adapter

        // Empty state
        binding.emptyText.visibility = if (favouriteList.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun loadFavourites() {
        val userId = auth.currentUser?.uid ?: return
        val ref = database.getReference("favourites").child(userId)

        binding.progressBar.visibility = android.view.View.VISIBLE

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                favouriteList.clear()
                for (favSnapshot in snapshot.children) {
                    val item = favSnapshot.getValue(ItemModel::class.java)
                    item?.let { favouriteList.add(it) }
                }
                adapter.notifyDataSetChanged()
                binding.progressBar.visibility = android.view.View.GONE
                binding.emptyText.visibility = if (favouriteList.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBar.visibility = android.view.View.GONE
                Toast.makeText(this@FavouritesActivity, "Failed to load favourites", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun removeFromFavourites(item: ItemModel) {
        val userId = auth.currentUser?.uid ?: return
        val favKey = item.title?.replace(" ", "")?.lowercase()?.takeIf { it.isNotEmpty() }
            ?: return

        database.getReference("favourites").child(userId).child(favKey).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Removed from favourites", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to remove", Toast.LENGTH_SHORT).show()
                }
            }
    }
}