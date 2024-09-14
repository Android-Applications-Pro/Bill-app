package com.example.billapp.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class GroupTransaction(
    val id: String = "",                      // 交易ID
    val name: String = "",
    val payer: List<String> = emptyList(),  // 付錢的人 (userId 列表)
    val divider: List<String> = emptyList(),  // 分錢的人 (userId 列表)
    val shareMethod: String = "",            // 分錢方法(均分、比例、份數、自訂)
    val type: String = "",                   // 交易類型 (支出, 收入)
    val amount: Double = 0.0,                // 交易金額
    val date: Timestamp? = null,             // 交易日期
    val createdAt: Timestamp? = null,        // 記錄創建的時間戳
    val updatedAt: Timestamp? = null         // 記錄更新的時間戳
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: emptyList(),
        parcel.createStringArrayList() ?: emptyList(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readParcelable(Timestamp::class.java.classLoader),
        parcel.readParcelable(Timestamp::class.java.classLoader),
        parcel.readParcelable(Timestamp::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeStringList(payer)
        parcel.writeStringList(divider)
        parcel.writeString(shareMethod)
        parcel.writeString(type)
        parcel.writeDouble(amount)
        parcel.writeParcelable(date, flags)
        parcel.writeParcelable(createdAt, flags)
        parcel.writeParcelable(updatedAt, flags)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<GroupTransaction> {
        override fun createFromParcel(parcel: Parcel): GroupTransaction {
            return GroupTransaction(parcel)
        }

        override fun newArray(size: Int): Array<GroupTransaction?> {
            return arrayOfNulls(size)
        }
    }
}
