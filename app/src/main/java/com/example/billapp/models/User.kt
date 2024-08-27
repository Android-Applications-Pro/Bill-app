package com.example.billapp.models

import android.os.Parcel
import android.os.Parcelable

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val image: String = "",
    val transactions: List<PersonalTransaction> = emptyList(),
    val income: Double = 0.0,
    val expense: Double = 0.0,
    var groupsID: MutableList<String> = mutableListOf() // 包含 User 的群組列表
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        mutableListOf<PersonalTransaction>().apply {
            parcel.readList(this, PersonalTransaction::class.java.classLoader)
        },
        parcel.readDouble(),
        parcel.readDouble(),
        mutableListOf<String>().apply {
            parcel.readStringList(this)
        }
    )

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(name)
        dest.writeString(email)
        dest.writeString(image)
        dest.writeList(transactions)
        dest.writeDouble(income)
        dest.writeDouble(expense)
        dest.writeStringList(groupsID) // Ensure groupsID is written
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
