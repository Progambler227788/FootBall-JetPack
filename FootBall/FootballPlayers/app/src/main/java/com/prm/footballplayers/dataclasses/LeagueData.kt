package com.prm.footballplayers.dataclasses

import android.os.Parcel
import android.os.Parcelable

data class LeagueData(
    val idLeague: String,
    val strLeague: String,
    val strSport: String,
    val strLeagueAlternate: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idLeague)
        parcel.writeString(strLeague)
        parcel.writeString(strSport)
        parcel.writeString(strLeagueAlternate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LeagueData> {
        override fun createFromParcel(parcel: Parcel): LeagueData {
            return LeagueData(parcel)
        }

        override fun newArray(size: Int): Array<LeagueData?> {
            return arrayOfNulls(size)
        }
    }
}

