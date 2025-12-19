package com.example.services_app.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.services_app.domain.BannerModel
import com.example.services_app.domain.CategoryModel
import com.example.services_app.domain.ItemModel
import com.example.services_app.domain.ProfileModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class MainRepository {
    private val firebaseDatabase= FirebaseDatabase.getInstance()

    private val _profile= MutableLiveData<ProfileModel>()
    private val _category = MutableLiveData<List<CategoryModel>>()
    private val _banner = MutableLiveData<List<BannerModel>>()

    val profile: LiveData<ProfileModel> get() = _profile
    val category: LiveData<List<CategoryModel>>get() = _category
    val banner: LiveData<List<BannerModel>>get() = _banner

    fun loadProfile() {
        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) return

        val ref = firebaseDatabase.getReference("users/$userId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // ✅ Name: Safe handling (nullable → non-nullable with fallback)
                val name = snapshot.child("name").getValue(String::class.java) ?: ""

                // 🔸 ProfilePic: EXACTLY as before — no change, no default override
                val pic = snapshot.child("profilePic").getValue(String::class.java)

                // Pass both to ProfileModel
                _profile.value = ProfileModel(name = name, profilePic = pic)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to load profile: ${error.message}")
            }
        })
    }

    fun loadCategory(){
        val ref = firebaseDatabase.getReference("Category")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list=mutableListOf<CategoryModel>()
                for (child in snapshot.children){
                  child.getValue(CategoryModel::class.java)?.let {
                      list.add(it)
                  }
                }
                _category.value=list
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun loadBanner () {
        val ref = firebaseDatabase.getReference("Banner")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot){
                val list = mutableListOf<BannerModel>()
                for (child in snapshot.children){
                    child.getValue(BannerModel::class.java)?.let{
                        list.add(it)
                    }
                }
                _banner.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    fun loadItemCategory(categoryId: String): LiveData<MutableList<ItemModel>> {
        val itemsLiveData = MutableLiveData<MutableList<ItemModel>>()

        if (categoryId == "0") {
            // Load ALL items and filter by showInAll == true
            val ref = firebaseDatabase.getReference("Items")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<ItemModel>()
                    for (childSnapshot in snapshot.children) {
                        val item = childSnapshot.getValue(ItemModel::class.java)
                        if (item?.showInAll == true) {
                            list.add(item)
                        }
                    }
                    itemsLiveData.value = list
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                    println("Failed to load all items: ${error.message}")
                    itemsLiveData.value = mutableListOf()
                }
            })
        } else {
            // Normal category filtering
            val ref = firebaseDatabase.getReference("Items")
            val query = ref.orderByChild("categoryId").equalTo(categoryId)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<ItemModel>()
                    for (childSnapshot in snapshot.children) {
                        val item = childSnapshot.getValue(ItemModel::class.java)
                        item?.let { list.add(it) }
                    }
                    itemsLiveData.value = list
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Failed to load items for category $categoryId: ${error.message}")
                    itemsLiveData.value = mutableListOf()
                }
            })
        }

        return itemsLiveData
    }

}