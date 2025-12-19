package com.example.services_app.domain

import java.io.Serializable

data class ItemModel(
    val title: String? = null,
    val subtitle: String? = null,
    val description: String? = null,
    val picUrl: String? = null,
    val profilePic: String? = null,
    val price: Int? = null,
    val oldPrice: Int? = null,
    val off: Int? = null,
    val classicPrice: Int? = null,
    val classicOldPrice: Int? = null,
    val premiumPrice: Int? = null,
    val premiumOldPrice: Int? = null,
    val platinumPrice: Int? = null,
    val platinumOldPrice: Int? = null,
    val name: String? = null,
    val job: String? = null,
    val categoryId: String? = null,
    val phone: String? = null,
    val showInAll: Boolean = false  // 👈 ADD THIS LINE
) : Serializable