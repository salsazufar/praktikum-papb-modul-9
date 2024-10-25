package com.example.modul2.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.modul2.data.model.local.Tugas
import com.example.modul2.data.model.local.TugasRepository
import com.example.modul2.viewmodel.TugasViewModel
import com.example.modul2.viewmodel.TugasViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TugasScreen(
    tugasRepository: TugasRepository,
    onNavigateBack: () -> Unit
) {
    val tugasViewModel: TugasViewModel = viewModel(factory = TugasViewModelFactory(tugasRepository))
    var matkul by remember { mutableStateOf(TextFieldValue("")) }
    var detail_tugas by remember { mutableStateOf(TextFieldValue("")) }
    val listTugas by tugasViewModel.listTugas.observeAsState(emptyList())
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Memisahkan tugas yang selesai dan belum selesai
    val ongoingTasks = listTugas.filter { !it.selesai }
    val completedTasks = listTugas.filter { it.selesai }
    var isCompletedVisible by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Spacer(modifier = Modifier.height(30.dp))
            TextField(
                value = matkul,
                onValueChange = { matkul = it },
                label = { Text("Nama Matkul") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = detail_tugas,
                onValueChange = { detail_tugas = it },
                label = { Text("Detail Tugas") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (matkul.text.isNotEmpty() && detail_tugas.text.isNotEmpty()) {
                        tugasViewModel.addTugas(matkul.text, detail_tugas.text)
                        scope.launch {
                            snackbarHostState.showSnackbar("Tugas berhasil ditambahkan")
                        }
                        matkul = TextFieldValue("")
                        detail_tugas = TextFieldValue("")
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Nama matkul dan detail tugas harus diisi")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Tambah Tugas")
            }
            SnackbarHost(hostState = snackbarHostState)

            Spacer(modifier = Modifier.height(16.dp))

            if (listTugas.isNotEmpty()) {
                LazyColumn {
                    // Tugas yang belum selesai
                    items(ongoingTasks) { tugas ->
                        TugasItem(
                            tugas = tugas,
                            onDoneClicked = { done ->
                                tugasViewModel.updateTugasCompletion(tugas.id, done)
                            }
                        )
                    }

                    // Section Selesai
                    if (completedTasks.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isCompletedVisible = !isCompletedVisible }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Selesai (${completedTasks.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Toggle completed",
                                    modifier = Modifier.rotate(if (isCompletedVisible) 180f else 0f)
                                )
                            }
                        }

                        // Tugas yang sudah selesai
                        if (isCompletedVisible) {
                            items(completedTasks) { tugas ->
                                TugasItem(
                                    tugas = tugas,
                                    onDoneClicked = { done ->
                                        tugasViewModel.updateTugasCompletion(tugas.id, done)
                                    },
                                    isCompleted = true,
                                    onDeleteClick = {
                                        // Implementasi hapus tugas
                                        tugasViewModel.deleteTugas(tugas.id)
                                    }
                                )
                            }
                        }
                    }
                }
            } else {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Tidak ada tugas yang ditambahkan",
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun TugasItem(
    tugas: Tugas,
    onDoneClicked: (Boolean) -> Unit,
    isCompleted: Boolean = false,
    onDeleteClick: (() -> Unit)? = null
) {
    var isDone by remember { mutableStateOf(tugas.selesai) }
    val checkboxColor = if (isDone) MaterialTheme.colorScheme.primary else Color.Gray

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDone) Color(0xFFEEEEEE) else MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDone) 0.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .border(2.dp, checkboxColor, CircleShape)
                    .background(if (isDone) checkboxColor else Color.Transparent)
                    .clickable {
                        isDone = !isDone
                        onDoneClicked(isDone)
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isDone) {
                    Text(
                        text = "✓",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = tugas.matkul,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                )
                if (tugas.detail_tugas.isNotEmpty()) {
                    Text(
                        text = tugas.detail_tugas,
                        style = MaterialTheme.typography.bodyMedium,
                        textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            if (isCompleted && onDeleteClick != null) {
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus tugas",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}