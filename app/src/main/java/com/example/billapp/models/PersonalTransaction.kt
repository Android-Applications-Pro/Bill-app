package com.example.billapp.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class PersonalTransaction(
    val transactionId: String = "",        // 交易記錄的唯一標識符
    val userId: String = "",               // 關聯的使用者 ID
    val type: String = "",                 // 交易類型 (支出, 收入)
    val amount: Double = 0.0,              // 交易金額
    val category: TransactionCategory = TransactionCategory.OTHER, // 交易分類
    val note: String? = null,              // 備註 (可選)
    val name: String = "",              // 交易名稱 (可選)
    val date: Timestamp? = null,           // 交易日期
    val createdAt: Timestamp? = null,      // 記錄創建的時間戳
    val updatedAt: Timestamp? = null,      // 記錄更新的時間戳
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        TransactionCategory.valueOf(parcel.readString()!!),  // 讀取 enum
        parcel.readString(),
        parcel.readString()!!,
        parcel.readParcelable(Timestamp::class.java.classLoader),  // 讀取 Timestamp
        parcel.readParcelable(Timestamp::class.java.classLoader),
        parcel.readParcelable(Timestamp::class.java.classLoader),

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(transactionId)
        parcel.writeString(userId)
        parcel.writeString(type)
        parcel.writeDouble(amount)
        parcel.writeString(category.name)  // 寫入 enum 的 name
        parcel.writeString(note)
        parcel.writeString(name)
        parcel.writeParcelable(date, flags)  // 寫入 Timestamp
        parcel.writeParcelable(createdAt, flags)
        parcel.writeParcelable(updatedAt, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PersonalTransaction> {
        override fun createFromParcel(parcel: Parcel): PersonalTransaction {
            return PersonalTransaction(parcel)
        }

        override fun newArray(size: Int): Array<PersonalTransaction?> {
            return arrayOfNulls(size)
        }
    }
}