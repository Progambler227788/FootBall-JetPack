@file:OptIn(ExperimentalCoilApi::class)

package com.prm.footballplayers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.room.Room
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.prm.footballplayers.databases.ClubDatabase
import com.prm.footballplayers.dataclasses.JerseyLookupResponse
import com.prm.footballplayers.dataclasses.ListLeaguesData
import com.prm.footballplayers.entities.Clubs
import com.prm.footballplayers.ui.theme.FootballPlayersTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
class SearchClub : ComponentActivity() {
    private lateinit var database: ClubDatabase
    private var searchQuery by mutableStateOf("")
    private var searchResult by mutableStateOf<List<Clubs>>(emptyList())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState?.let { bundle ->
            searchResult = bundle.getParcelableArrayList("clubsList") ?: emptyList()
            searchQuery = bundle.getString("searchQuery") ?: ""
        }
        database =
            Room.databaseBuilder(applicationContext, ClubDatabase::class.java, "football_clubs.db")
                .build()



        setContent {
            FootballPlayersTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize()
                    ) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Enter search query") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                // Perform search and update search result
                                CoroutineScope(Dispatchers.Main).launch {
                                    try {
                                        // Perform the search operation asynchronously
                                        searchResult = searchClubs(searchQuery)
                                        println("Search result size: ${searchResult.size}")
                                    } catch (e: Exception) {
                                        println("Error occurred during search: $e")
                                    }
                                }
                            },
                            modifier = Modifier.align(Alignment.End),
                            shape = RoundedCornerShape(topStart = 8.dp, bottomEnd = 15.dp)
                        ) {
                            Text("Search")
                        }


                        Spacer(modifier = Modifier.height(16.dp))

                        // Display search results in a LazyColumn
                        LazyColumn {
                            // Assuming ClubItem composable function accepts a Club type
                            items(searchResult) { club ->
                                ClubItem(club)
                                Divider() // Add a divider between items
                            }
                        }
                    }
                }
            }
        }
    }
    @ExperimentalCoilApi
    @Composable
    fun ClubItem(club: Clubs) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Display Team Logo image
            Image(
                painter = rememberImagePainter(club.teamLogo),
                contentDescription = null,
                modifier = Modifier.size(100.dp) // 100 size
            )
            Text("Club Name: ${club.name}", fontWeight = FontWeight.Bold)
            Text("Short Name: ${club.shortName}")
            Text("Alternate Name: ${club.alternateName}")
            Text("Formed Year: ${club.formedYear}")
            Text("League: ${club.league}")
            Text("Stadium: ${club.stadium}")
            Text("Keywords: ${club.keywords}")
        }
    }


    // Inside your SearchClub class
    private suspend fun searchClubs(query: String): List<Clubs> {
        return withContext(Dispatchers.IO) {
            println(query)
            database.clubDao().searchClubs("%$query%") // Perform database operation off the main thread
        }
    }
    // Override onSaveInstanceState to save the state
    override fun onSaveInstanceState(outState: Bundle) {
        // Save state
        outState.putString("searchQuery", searchQuery)
        outState.putParcelableArrayList("clubsList", ArrayList(searchResult))
        super.onSaveInstanceState(outState)
    }

    // Override onRestoreInstanceState to restore the state
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchResult = savedInstanceState.getParcelableArrayList("clubsList") ?: emptyList()
        // Restore state
        searchQuery = savedInstanceState.getString("searchQuery") ?: ""
    }
}
