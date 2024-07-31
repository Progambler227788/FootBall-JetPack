package com.prm.footballplayers.dataclasses

import android.os.Parcel
import android.os.Parcelable

data class JerseyData(
    val idEquipment: String,
    val idTeam: String,
    val date: String,
    val strSeason: String,
    val strEquipment: String,
    val strType: String,
    val strUsername: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idEquipment)
        parcel.writeString(idTeam)
        parcel.writeString(date)
        parcel.writeString(strSeason)
        parcel.writeString(strEquipment)
        parcel.writeString(strType)
        parcel.writeString(strUsername)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<JerseyData> {
        override fun createFromParcel(parcel: Parcel): JerseyData {
            return JerseyData(parcel)
        }

        override fun newArray(size: Int): Array<JerseyData?> {
            return arrayOfNulls(size)
        }
    }
}

