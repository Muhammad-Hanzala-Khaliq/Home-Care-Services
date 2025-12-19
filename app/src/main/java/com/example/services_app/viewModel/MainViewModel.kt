package com.example.services_app.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.services_app.domain.BannerModel
import com.example.services_app.domain.CategoryModel
import com.example.services_app.domain.ProfileModel
import com.example.services_app.repository.MainRepository

class MainViewModel: ViewModel() {
    private val repository= MainRepository()

    val profile: LiveData<ProfileModel> = repository.profile
    val category: LiveData<List<CategoryModel>> = repository.category
    val banner: LiveData<List<BannerModel>> = repository.banner
     fun loadProfile() = repository.loadProfile()
    fun loadCategories() = repository.loadCategory()
    fun loadBanner() = repository.loadBanner()
    fun loadItems(categoryId:String)= repository.loadItemCategory(categoryId)

}