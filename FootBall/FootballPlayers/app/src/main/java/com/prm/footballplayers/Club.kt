package com.prm.footballplayers

import android.os.Parcel
import android.os.Parcelable

data class Club(
    val idTeam: String,
    val strTeam: String,
    val strTeamShort: String,
    val strAlternate: String,
    val intFormedYear: String,
    val strLeague: String,
    val idLeague: String,
    val strStadium: String,
    val strKeywords: String,
    val strStadiumThumb: String,
    val strStadiumLocation: String,
    val intStadiumCapacity: String,
    val strWebsite: String,
    val strTeamJersey: String,
    val strTeamLogo: String,
    val strDescriptionEN: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idTeam)
        parcel.writeString(strTeam)
        parcel.writeString(strTeamShort)
        parcel.writeString(strAlternate)
        parcel.writeString(intFormedYear)
        parcel.writeString(strLeague)
        parcel.writeString(idLeague)
        parcel.writeString(strStadium)
        parcel.writeString(strKeywords)
        parcel.writeString(strStadiumThumb)
        parcel.writeString(strStadiumLocation)
        parcel.writeString(intStadiumCapacity)
        parcel.writeString(strWebsite)
        parcel.writeString(strTeamJersey)
        parcel.writeString(strTeamLogo)
        parcel.writeString(strDescriptionEN)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Club> {
        override fun createFromParcel(parcel: Parcel): Club {
            return Club(parcel)
        }

        override fun newArray(size: Int): Array<Club?> {
            return arrayOfNulls(size)
        }
    }
}


