package com.prm.footballplayers

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.room.Room
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.gson.Gson
import com.prm.footballplayers.databases.ClubDatabase
import com.prm.footballplayers.dataclasses.JerseyLookupResponse
import com.prm.footballplayers.dataclasses.ListLeaguesData
import com.prm.footballplayers.entities.Clubs
import com.prm.footballplayers.ui.theme.FootballPlayersTheme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
class SearchClubsByLeague : ComponentActivity() {
    private lateinit var database: ClubDatabase
    private var clubResponse by mutableStateOf((ClubResponse((emptyList()))))
    private var leagueInput by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let { bundle ->

            leagueInput = bundle.getString("leagueInput") ?: ""
            clubResponse = ClubResponse(bundle.getParcelableArrayList("clubsListLeague") ?: emptyList())
        }
        // Get database
        database =
            Room.databaseBuilder(applicationContext, ClubDatabase::class.java, "football_clubs.db")
                .build()
        setContent {
            FootballPlayersTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Text Box for entering league name
                        TextField(
                            value = leagueInput,
                            onValueChange = { leagueInput = it },
                            label = { Text("Enter League Name") }
                        )

                        // Buttons placed horizontally
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Button(
                                onClick = {
                                    // Handle Retrieve Clubs button click
                                    val leagueName = leagueInput
                                    clubResponse = ClubResponse(emptyList())
                                    retrieveClubs(leagueName)
                                },
                                shape = RoundedCornerShape(topStart = 8.dp, bottomEnd = 15.dp)
                            ) {
                                Text("Retrieve Clubs")
                            }

                            Button(
                                onClick = {

                                    // Save the retrieved club details to the local database
                                    saveClubsToDatabase(clubResponse.teams)
                                },
                                shape = RoundedCornerShape(topStart = 8.dp, bottomEnd = 15.dp)
                            ) {
                                Text("Save clubs to Database")
                            }
                        }

                        // Display club details
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            clubResponse.teams.let { teams ->
                                items(teams) { club ->
                                    ClubItem(club = club)
                                }
                            }
                        }
                    }
                }
            }
        }

    }



// Club Item Code for displaying list item
    @OptIn(ExperimentalCoilApi::class)
    @Composable
    fun ClubItem(club: Club) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Club Name: ${club.strTeam}", fontWeight = FontWeight.Bold)
            Text("Short Name: ${club.strTeamShort}")
            Text("Alternate Name: ${club.strAlternate}")
            Text("Formed Year: ${club.intFormedYear}")
            Text("League: ${club.strLeague}")
            Text("League ID: ${club.idLeague}")
            Text("Stadium: ${club.strStadium}")
            Text("Keywords: ${club.strKeywords}")
            // Display Stadium Thumb image
            Image(
                painter = rememberImagePainter(club.strStadiumThumb),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
            Text("Stadium Location: ${club.strStadiumLocation}")
            Text("Stadium Capacity: ${club.intStadiumCapacity}")
            Text("Website: ${club.strWebsite}")
            // Display Team Jersey image
            Image(
                painter = rememberImagePainter(club.strTeamJersey),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )

            // Display Team Logo image
            Image(
                painter = rememberImagePainter(club.strTeamLogo),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
            Text("Description: ${club.strDescriptionEN}")
        }
    }

    private fun retrieveClubs(leagueName: String) {
        println(leagueName)
        val apiUrl = "https://www.thesportsdb.com/api/v1/json/3/search_all_teams.php?l=$leagueName"

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL(apiUrl)
                val urlConnection = url.openConnection() as HttpURLConnection
                val inputStream = urlConnection.inputStream
                val reader = InputStreamReader(inputStream)
                val data = reader.readText()

                // Parse JSON response
                val gson = Gson()
                val newClubs = gson.fromJson(data, ClubResponse::class.java)
                clubResponse = newClubs

                newClubs.teams.let { teams ->
                    if (teams.isNotEmpty()) {
                        teams.forEach { _ ->

                            newClubs.teams.forEach { club ->
                                val clubDetails = Club(
                                    idTeam = club.idTeam,
                                    strTeam = club.strTeam,
                                    strTeamShort = club.strTeamShort,
                                    strAlternate = club.strAlternate,
                                    intFormedYear = club.intFormedYear,
                                    strLeague = club.strLeague,
                                    idLeague = club.idLeague,
                                    strStadium = club.strStadium,
                                    strKeywords = club.strKeywords,
                                    strStadiumThumb = club.strStadiumThumb,
                                    strStadiumLocation = club.strStadiumLocation,
                                    intStadiumCapacity = club.intStadiumCapacity,
                                    strWebsite = club.strWebsite,
                                    strTeamJersey = club.strTeamJersey,
                                    strTeamLogo = club.strTeamLogo,
                                    strDescriptionEN = club.strDescriptionEN
                                )
                                // For testing purposes, let's print the club details to the logcat
                                println("Club Details:")
                                println("ID: ${clubDetails.idTeam}")
                                println("Name: ${clubDetails.strTeam}")
                                println("Short Name: ${clubDetails.strTeamShort}")
                                println("Alternate Name: ${clubDetails.strAlternate}")
                                println("Formed Year: ${clubDetails.intFormedYear}")
                                println("League: ${clubDetails.strLeague}")
                                println("League ID: ${clubDetails.idLeague}")
                                println("Stadium: ${clubDetails.strStadium}")
                                println("Keywords: ${clubDetails.strKeywords}")
                                println("Stadium Thumb: ${clubDetails.strStadiumThumb}")
                                println("Stadium Location: ${clubDetails.strStadiumLocation}")
                                println("Stadium Capacity: ${clubDetails.intStadiumCapacity}")
                                println("Website: ${clubDetails.strWebsite}")
                                println("Team Jersey: ${clubDetails.strTeamJersey}")
                                println("Team Logo: ${clubDetails.strTeamLogo}")
                                println("Description: ${clubDetails.strDescriptionEN}")
                                println("-----------------------------------")
                            }
                        }
                    } else {
                        println("No clubs found for the provided league.")
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Inside SearchClubsByLeague class
    private fun saveClubsToDatabase(clubs: List<Club>) {
        val clubEntities = clubs.map { it.toEntity() }

        GlobalScope.launch(Dispatchers.IO) {
            database.clubDao().insertAllClubs(clubEntities)
        }
        Toast.makeText(this@SearchClubsByLeague,"Clubs Saved",Toast.LENGTH_SHORT).show()
    }
    private fun Club.toEntity(): Clubs {
        return Clubs(
            idTeam = this.idTeam,
            name = this.strTeam,
            shortName = this.strTeamShort ?: "",
            alternateName = this.strAlternate ,
            formedYear = this.intFormedYear.toIntOrNull() ?: 0, // Convert to Int or default value
            league = this.strLeague?: "",
            leagueId = this.idLeague.toIntOrNull() ?: 0, // Convert to Int or provide default value
            stadium = this.strStadium?: "", // a default value if strStadium is null
            keywords = this.strKeywords?: "", // a default value if strKeywords is null
            stadiumThumb = this.strStadiumThumb?: "", //  a default value if strStadiumThumb is null
            stadiumLocation = this.strStadiumLocation, // a default value if strStadiumLocation is null
            stadiumCapacity = this.intStadiumCapacity.toIntOrNull() ?: 0, // Convert to Int or provide default value
            website = this.strWebsite?: "", //  a default value if strWebsite is null
            teamJersey = this.strTeamJersey?: "", //  a default value if strTeamJersey is null
            teamLogo = this.strTeamLogo?: "", // a default value if strTeamLogo is null
            description = this.strDescriptionEN ?: "" //  a default value if strDescriptionEN is null
        )
    }
    // Override onSaveInstanceState to save the state
    override fun onSaveInstanceState(outState: Bundle) {
        // Save state
        outState.putString("leagueInput", leagueInput)
        outState.putParcelableArrayList("clubsListLeague", ArrayList(clubResponse.teams))

        super.onSaveInstanceState(outState)
    }

    // Override onRestoreInstanceState to restore the state
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        clubResponse = ClubResponse(savedInstanceState.getParcelableArrayList("clubsListLeague") ?: emptyList())
        // Restore state
        leagueInput = savedInstanceState.getString("leagueInput") ?: ""
    }


}
