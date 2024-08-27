package com.example.billapp.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class Group(
    val id: String = "",
    val name: String = "",
    val image: String = "",
    val createdBy: String = "",
    var assignedTo: MutableList<String> = mutableListOf(),  // List of user id
    val transactions: MutableList<GroupTransaction> = mutableListOf(),  // 未結清交易清單
    val closedTransactions: MutableList<GroupTransaction> = mutableListOf(),  // 已結清交易清單
    val deptRelations: MutableList<DeptRelation> = mutableListOf(), // 個人之間的欠債關係，需要藉由計算每一筆群組交易獲得
    val createdTime : Timestamp ?= null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.createTypedArrayList(GroupTransaction.CREATOR)!!,
        parcel.createTypedArrayList(GroupTransaction.CREATOR)!!,
        parcel.createTypedArrayList(DeptRelation.CREATOR)!!,
        parcel.readParcelable(Timestamp::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeString(createdBy)
        parcel.writeStringList(assignedTo)
        parcel.writeTypedList(transactions)
        parcel.writeTypedList(closedTransactions)
        parcel.writeTypedList(deptRelations)
        parcel.writeParcelable(createdTime, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Group> {
        override fun createFromParcel(parcel: Parcel): Group {
            return Group(parcel)
        }

        override fun newArray(size: Int): Array<Group?> {
            return arrayOfNulls(size)
        }
    }
}
