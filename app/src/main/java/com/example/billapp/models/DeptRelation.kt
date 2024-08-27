package com.example.billapp.models

import android.os.Parcel
import android.os.Parcelable

data class DeptRelation(
    val id: String = "",
    val groupTransactionId: String = "",
    val from: String = "",
    val to: String = "",
    val amount : Double = 0.0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(groupTransactionId)
        parcel.writeString(from)
        parcel.writeString(to)
        parcel.writeDouble(amount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DeptRelation> {
        override fun createFromParcel(parcel: Parcel): DeptRelation {
            return DeptRelation(parcel)
        }

        override fun newArray(size: Int): Array<DeptRelation?> {
            return arrayOfNulls(size)
        }
    }
}
