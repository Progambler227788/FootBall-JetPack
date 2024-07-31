package com.prm.footballplayers.entities
import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clubs")
data class Clubs(
    @PrimaryKey val idTeam: String,
    val name: String,
    val shortName: String,
    val alternateName: String,
    val formedYear: Int,
    val league: String,
    val leagueId: Int,
    val stadium: String,
    val keywords: String,
    val stadiumThumb: String,
    val stadiumLocation: String,
    val stadiumCapacity: Int,
    val website: String,
    val teamJersey: String,
    val teamLogo: String,
    val description: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idTeam)
        parcel.writeString(name)
        parcel.writeString(shortName)
        parcel.writeString(alternateName)
        parcel.writeInt(formedYear)
        parcel.writeString(league)
        parcel.writeInt(leagueId)
        parcel.writeString(stadium)
        parcel.writeString(keywords)
        parcel.writeString(stadiumThumb)
        parcel.writeString(stadiumLocation)
        parcel.writeInt(stadiumCapacity)
        parcel.writeString(website)
        parcel.writeString(teamJersey)
        parcel.writeString(teamLogo)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Clubs> {
        override fun createFromParcel(parcel: Parcel): Clubs {
            return Clubs(parcel)
        }

        override fun newArray(size: Int): Array<Clubs?> {
            return arrayOfNulls(size)
        }
    }
}
