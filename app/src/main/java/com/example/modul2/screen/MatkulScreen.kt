package com.example.modul2.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.modul2.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatkulScreen(
    onNavigateToProfile: () -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()
    var matkulList by remember { mutableStateOf<List<Matkul>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val githubIcon = painterResource(id = R.drawable.github)

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
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(
                            painter = githubIcon,
                            contentDescription = "GitHub Profile",
                            modifier = Modifier.size(24.dp)
                        )
                    }
//                    IconButton(onClick = onLogout) {
//                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
//                    }
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