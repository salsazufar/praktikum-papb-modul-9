package com.example.modul2

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ExitToApp
//import androidx.compose.material.icons.filled.GitHub
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch

class ListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ListScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen() {
    val firestore = FirebaseFirestore.getInstance()
    var matkulList by remember { mutableStateOf<List<Matkul>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val githubIcon: Painter = painterResource(id = R.drawable.github)

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val result = firestore.collection("matkul").get().await()
                matkulList = result.toObjects(Matkul::class.java)
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
                title = { Text("Daftar Mata Kuliah") },
                actions = {
                    IconButton(onClick = {
                        context.startActivity(Intent(context, GithubProfileActivity::class.java))
                    }) {
                        Icon(
                            painter = githubIcon,
                            contentDescription = "GitHub Profile",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        context.startActivity(Intent(context, MainActivity::class.java))
                        (context as? ComponentActivity)?.finish()
                    }) {
                        Icon(Icons.Filled.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                error != null -> {
                    Text(
                        text = "Error: $error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                matkulList.isEmpty() -> {
                    Text(
                        text = "Tidak ada mata kuliah",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn {
                        items(matkulList) { matkul ->
                            MatkulCard(matkul)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatkulCard(matkul: Matkul) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = matkul.matkul, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${matkul.hari}, ${matkul.jam}")
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Ruang: ${matkul.ruangan}")
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = if (matkul.praktikum) "Praktikum" else "Teori")
        }
    }
}

@Composable
fun GitHubIcon() {
    val githubIcon: Painter = painterResource(id = R.drawable.github)
    Icon(
        painter = githubIcon,
        contentDescription = "GitHub Profile",
        modifier = Modifier.size(48.dp)
    )
}
