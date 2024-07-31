package com.prm.footballplayers.dataclasses

import android.os.Parcel
import android.os.Parcelable

data class ClubData(
    val idTeam: String,
    val strTeam: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idTeam)
        parcel.writeString(strTeam)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ClubData> {
        override fun createFromParcel(parcel: Parcel): ClubData {
            return ClubData(parcel)
        }

        override fun newArray(size: Int): Array<ClubData?> {
            return arrayOfNulls(size)
        }
    }
}
