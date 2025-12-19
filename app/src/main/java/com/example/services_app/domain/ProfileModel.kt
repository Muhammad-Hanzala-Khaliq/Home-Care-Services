// domain/ProfileModel.kt
package com.example.services_app.domain

data class ProfileModel(
    val name: String = "",          // non-nullable with safe default
    val profilePic: String? = null  // ✅ Keep nullable — as you requested
)