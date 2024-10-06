package com.example.modul2

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

class GithubProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GithubProfileScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GithubProfileScreen() {
    var user by remember { mutableStateOf<GithubUser?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://api.github.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val service = retrofit.create(GithubService::class.java)
                user = service.getUser("salsazufar")
                isLoading = false
            } catch (e: Exception) {
                error = e.message
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GitHub Profile") },
                navigationIcon = {
                    IconButton(onClick = {
                        context.startActivity(Intent(context, ListActivity::class.java))
                        (context as? ComponentActivity)?.finish()
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back to List")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                error != null -> {
                    Text("Error: $error", color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                }
                user != null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(user!!.avatar_url),
                            contentDescription = "Profile picture",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = user!!.login,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = user!!.name ?: "",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedCard(
                                modifier = Modifier.weight(1f).padding(end = 4.dp)
                            ) {
                                Text(
                                    text = "Followers: ${user!!.followers}",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                            OutlinedCard(
                                modifier = Modifier.weight(1f).padding(start = 4.dp)
                            ) {
                                Text(
                                    text = "Following: ${user!!.following}",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class GithubUser(
    val login: String,
    val name: String?,
    val avatar_url: String,
    val followers: Int,
    val following: Int
)

interface GithubService {
    @GET("users/{username}")
    suspend fun getUser(@Path("username") username: String): GithubUser
}