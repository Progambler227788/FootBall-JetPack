@file:OptIn(ExperimentalCoilApi::class)

package com.prm.footballplayers

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.prm.footballplayers.databases.DatabaseLeague
import com.prm.footballplayers.entities.League
import com.prm.footballplayers.ui.theme.FootballPlayersTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.gson.Gson
import com.prm.footballplayers.dataclasses.JerseyData
import com.prm.footballplayers.dataclasses.JerseyLookupResponse
import com.prm.footballplayers.dataclasses.LeagueData
import com.prm.footballplayers.dataclasses.ListLeaguesData
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.delay
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


@OptIn(DelicateCoroutinesApi::class)
class MainActivity : ComponentActivity() {
    private lateinit var database: DatabaseLeague
    private var leaguesResponse  by mutableStateOf((ListLeaguesData(((emptyList())))))
    private var clubResponse by mutableStateOf((ClubResponse((emptyList()))))
    private var jerseyResponse by mutableStateOf(JerseyLookupResponse(emptyList()))
    private var searchQuery by mutableStateOf("")

    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore state if available
        savedInstanceState?.let { bundle ->
            jerseyResponse = JerseyLookupResponse(bundle.getParcelableArrayList("jerseyList") ?: emptyList())
            searchQuery = bundle.getString("searchQuery") ?: ""
            clubResponse = ClubResponse(bundle.getParcelableArrayList("clubList") ?: emptyList())
            leaguesResponse = ListLeaguesData(bundle.getParcelableArrayList("leagueList") ?: emptyList())

        }


        // Initialize Room database
        database = Room.databaseBuilder(applicationContext, DatabaseLeague::class.java, "football_leagues.db")
            .build()

        retrieveLeagues()

        setContent {
            FootballPlayersTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Content above the LazyColumn
                        Button(
                            onClick = { saveLeaguesToDatabase()
                                      },
                            modifier = Modifier.padding(16.dp),
                            shape = RoundedCornerShape(topStart = 8.dp, bottomEnd = 15.dp)
                        ) {
                            Text(text = "Add Leagues to DB")
                        }
                        Button(
                            onClick = { startActivity(Intent(this@MainActivity, SearchClubsByLeague::class.java)) },
                            modifier = Modifier.padding(16.dp),
                            shape = RoundedCornerShape(topStart = 8.dp, bottomEnd = 15.dp)
                        ) {
                            Text(text = "Search for Clubs By League")
                        }
                        Button(
                            onClick = { startActivity(Intent(this@MainActivity, SearchClub::class.java)) },
                            modifier = Modifier.padding(16.dp),
                            shape = RoundedCornerShape(topStart = 8.dp, bottomEnd = 15.dp)
                        ) {
                            Text(text = "Search for Clubs")
                        }

                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Enter club name i.e. Arsenal") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                // Perform search and retrieve clubs from web service
                                var id: String? = ""
                                GlobalScope.launch(Dispatchers.IO) {
                                    delay(1000) // Adjust the delay time as needed
                                    id = retrieveClubs(searchQuery)
                                    if (id != null) {
                                        retrieveJerseys(id!!)
                                    } else {
                                        println("Club not found")
                                    }
                                }
                            },
                            modifier = Modifier.padding(16.dp),
                            shape = RoundedCornerShape(topStart = 8.dp, bottomEnd = 15.dp)
                        ) {
                            Text("Search Club via Web Services")
                        }

                        // LazyColumn for jersey items
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f), // Expand to fill available space
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            jerseyResponse.equipment?.forEach { jersey ->
                                item {
                                    JerseyItem(jersey = jersey)
                                }
                            }

                            // Show message if no jerseys found
                            if (jerseyResponse?.equipment.isNullOrEmpty()) {
                                item {
                                    Text("No jerseys found")
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    @Composable
    fun JerseyItem(jersey  : JerseyData ){
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {


            Image(
                    painter = rememberImagePainter(jersey.strEquipment),
                    contentDescription = "Jersey",
                    modifier = Modifier.size(100.dp)
            )
            Text("Username: ${jersey .strUsername}", fontWeight = FontWeight.Bold)
            Text("Season: ${jersey.strSeason}")
            Text("Type: ${jersey .strType}")
            Text("Date: ${jersey .date}")
            Text("Id of jersey: ${jersey.idEquipment}")
        }
    }

    private fun saveLeaguesToDatabase() {
         val leagues = listOf(
            League("4328", "English Premier League", "Soccer", "Premier League, EPL"),
            League("4329", "English League Championship", "Soccer", "Championship"),
            League("4330", "Scottish Premier League", "Soccer", "Scottish Premiership, SPFL"),
            League("4331", "German Bundesliga", "Soccer", "Bundesliga, Fußball-Bundesliga"),
            League("4332", "Italian Serie A", "Soccer", "Serie A"),
            League("4334", "French Ligue 1", "Soccer", "Ligue 1 Conforama"),
            League("4335", "Spanish La Liga", "Soccer", "LaLiga Santander, La Liga"),
            League("4336", "Greek Superleague Greece", "Soccer", ""),
            League("4337", "Dutch Eredivisie", "Soccer", "Eredivisie"),
            League("4338", "Belgian First Division A", "Soccer", "Jupiler Pro League"),
            League("4339", "Turkish Super Lig", "Soccer", "Super Lig"),
            League("4340", "Danish Superliga", "Soccer", ""),
            League("4344", "Portuguese Primeira Liga", "Soccer", "Liga NOS"),
            League("4346", "American Major League Soccer", "Soccer", "MLS, Major League Soccer"),
            League("4347", "Swedish Allsvenskan", "Soccer", "Fotbollsallsvenskan"),
            League("4350", "Mexican Primera League", "Soccer", "Liga MX"),
            League("4351", "Brazilian Serie A", "Soccer", ""),
            League("4354", "Ukrainian Premier League", "Soccer", ""),
            League("4355", "Russian Football Premier League", "Soccer", "Чемпионат России по футболу"),
            League("4356", "Australian A-League", "Soccer", "A-League"),
            League("4358", "Norwegian Eliteserien", "Soccer", "Eliteserien"),
            League("4359", "Chinese Super League", "Soccer", "")
        )
        GlobalScope.launch(Dispatchers.IO) {
                database.leagueDao().insertLeagues(leagues)
            }
        Toast.makeText(this@MainActivity,"Done",Toast.LENGTH_SHORT).show()

        GlobalScope.launch(Dispatchers.IO) {
            val fetchedLeagues = database.leagueDao().getAllLeagues()
            for (league in fetchedLeagues) {
                println("League: " + league.strLeague)
            }
        }
    }

    private fun retrieveJerseys(id: String) {
        println(id)
        val apiUrl = "https://www.thesportsdb.com/api/v1/json/3/lookupequipment.php?id=${id}"

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL(apiUrl)
                val urlConnection = url.openConnection() as HttpURLConnection
                val inputStream = urlConnection.inputStream
                val reader = InputStreamReader(inputStream)
                val data = reader.readText()

                println("Response Code: ${urlConnection.responseCode}")


                // Parse JSON response
                val gson = Gson()
                val newJerseys = gson.fromJson(data, JerseyLookupResponse::class.java)
                println("JSON Response: $data")

                jerseyResponse = newJerseys

                println("Received jerseys: ${newJerseys.equipment}")
                println("Jerseys size: ${newJerseys.equipment?.size}")

                newJerseys.equipment.let { jerseys ->
                    if (jerseys.isNotEmpty()) {
                        jerseys.forEach { _ ->


                            newJerseys.equipment.forEach { jersey ->
                                val jerseyDetails = JerseyData(
                                    idEquipment = jersey.idEquipment ?: "",
                                    idTeam = jersey.idTeam ?: "",
                                    date= jersey.date ?: "",
                                    strSeason = jersey.strSeason ?: "",
                                    strEquipment = jersey.strEquipment ?: "",
                                    strType = jersey.strType ?: "",
                                    strUsername= jersey.strUsername ?: ""

                                )
                                // For testing purposes, let's print the club details to the logcat
                                println("Jersey Details:")
                                println("ID: ${jerseyDetails.idTeam}")
                                println("Name: ${jerseyDetails.strSeason}")

                            }
                        }
                    } else {
                        println("No jerseys found for the provided Club ID.")
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun retrieveLeagues() {

        val apiUrl = "https://www.thesportsdb.com/api/v1/json/3/all_leagues.php"

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL(apiUrl)
                val urlConnection = url.openConnection() as HttpURLConnection
                val inputStream = urlConnection.inputStream
                val reader = InputStreamReader(inputStream)
                val data = reader.readText()

                println("Response Code: ${urlConnection.responseCode}")


                // Parse JSON response
                val gson = Gson()
                val newLeagues = gson.fromJson(data, ListLeaguesData::class.java)
                println("JSON Response: $data")

                leaguesResponse = newLeagues

                println("Received Leagues: ${newLeagues .leagues.size}")


                newLeagues.leagues.let { leagues ->
                    if (leagues.isNotEmpty()) {
                        leagues.forEach { _ ->


                            newLeagues.leagues.forEach { league ->
                                val leagueDetails = LeagueData(
                                    idLeague = league.idLeague,
                                    strLeague= league.strLeague,
                                    strSport= league.strSport,
                                    strLeagueAlternate= league.strLeagueAlternate

                                )
                                // For testing purposes, let's print the club details to the logcat
                                println("League Details:")
                                println("ID: ${leagueDetails .idLeague}")


                            }
                        }
                    } else {
                        println("No leagues found")
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun retrieveClubs(nameOfClub: String): String? {
        leaguesResponse?.leagues?.forEach { league ->
            val leagueName = league.strLeague
            println("In Club function: $leagueName" )

            val apiUrl = "https://www.thesportsdb.com/api/v1/json/3/search_all_teams.php?l=$leagueName"

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

                newClubs.teams?.forEach { club ->
                    if (club.strTeam == nameOfClub) {
                        return club.idTeam // Return the ID directly
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null // Return null if club not found
    }

    // Override onSaveInstanceState to save the state
    override fun onSaveInstanceState(outState: Bundle) {
        // Save state
        outState.putString("searchQuery", searchQuery)
        outState.putParcelableArrayList("jerseyList", ArrayList(jerseyResponse.equipment))
        outState.putParcelableArrayList("clubList", ArrayList(clubResponse.teams))
        outState.putParcelableArrayList("leagueList", ArrayList(leaguesResponse.leagues))
        super.onSaveInstanceState(outState)
    }

    // Override onRestoreInstanceState to restore the state
    @Suppress("DEPRECATION")
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        jerseyResponse = JerseyLookupResponse(savedInstanceState.getParcelableArrayList("jerseyList") ?: emptyList())
        clubResponse = ClubResponse(savedInstanceState.getParcelableArrayList("clubList") ?: emptyList())
        leaguesResponse = ListLeaguesData(savedInstanceState.getParcelableArrayList("leagueList") ?: emptyList())
        // Restore state
        searchQuery = savedInstanceState.getString("searchQuery") ?: ""
    }


}
