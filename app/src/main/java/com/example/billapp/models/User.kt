package com.example.billapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val image: String = "",
    val transactions: List<PersonalTransaction> = emptyList(),
    val income: Double = 0.0,
    val expense: Double = 0.0,
    var groupsID: MutableList<String> = mutableListOf(),
    var experience: Int = 0,
    var trustLevel: Int = 100
) : Parcelable