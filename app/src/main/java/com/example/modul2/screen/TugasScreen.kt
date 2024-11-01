package com.example.modul2.screen

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.example.modul2.data.model.local.Tugas
import com.example.modul2.data.model.local.TugasRepository
import com.example.modul2.viewmodel.TugasViewModel
import com.example.modul2.viewmodel.TugasViewModelFactory
import kotlinx.coroutines.launch
import java.io.File

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
    var imagePath by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    var previewImagePath by remember { mutableStateOf<String?>(null) }
    var isCompletedVisible by remember { mutableStateOf(true) }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let {
                val file = File(context.filesDir, "task_${System.currentTimeMillis()}.jpg")
                file.outputStream()
                    .use { out -> bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out) }
                imagePath = Uri.fromFile(file)
            }
        }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launcher.launch()
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Camera permission is required")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        TextField(
            value = matkul,
            onValueChange = { matkul = it },
            label = { Text("Nama Mata Kuliah") },
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

        imagePath?.let { uri ->
            Image(
                painter = rememberImagePainter(uri),
                contentDescription = "Tugas Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .size(200.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color = Color.Gray)
                    .padding(9.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        launcher.launch()
                    } else {
                        cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Ambil Gambar")
            }

            Button(
                onClick = {
                    if (matkul.text.isNotEmpty() && detail_tugas.text.isNotEmpty()) {
                        tugasViewModel.addTugas(matkul.text, detail_tugas.text, imagePath?.path)
                        scope.launch {
                            snackbarHostState.showSnackbar("Tugas Ditambahkan")
                        }
                        matkul = TextFieldValue("")
                        detail_tugas = TextFieldValue("")
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Nama matkul dan detail tugas harus diisi")
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Tambah Tugas")
            }
        }
        SnackbarHost(hostState = snackbarHostState)

        if (listTugas.isNotEmpty()) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                // Tugas yang belum selesai
                items(listTugas.filter { !it.selesai }) { tugas ->
                    TugasItem(
                        tugas = tugas,
                        onComplete = { completed ->
                            tugasViewModel.updateTugasCompletion(tugas.id, completed)
                        },
                        isCompleted = false,
                        onDelete = null,
                        onPreviewImage = { imagePath -> previewImagePath = imagePath }
                    )
                }

                // Section Selesai
                if (listTugas.any { it.selesai }) {
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
                                text = "Selesai (${listTugas.count { it.selesai }})",
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

                    if (isCompletedVisible) {
                        items(listTugas.filter { it.selesai }) { tugas ->
                            TugasItem(
                                tugas = tugas,
                                onComplete = { completed ->
                                    tugasViewModel.updateTugasCompletion(tugas.id, completed)
                                },
                                isCompleted = true,
                                onDelete = {
                                    tugasViewModel.deleteTugas(tugas.id)
                                },
                                onPreviewImage = { imagePath -> previewImagePath = imagePath }
                            )
                        }
                    }
                }
            }
        } else {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "Mulai Tambahkan Tugas!",
                fontSize = 16.sp
            )
        }

        previewImagePath?.let { uri ->
            ImagePreviewDialog(uri) {
                previewImagePath = null
            }
        }
    }
}

@Composable
fun TugasItem(
    tugas: Tugas,
    onComplete: (Boolean) -> Unit,
    isCompleted: Boolean,
    onDelete: (() -> Unit)?,
    onPreviewImage: (String) -> Unit
) {
    var isDone by remember { mutableStateOf(tugas.selesai) }
    val checkboxColor = if (isDone) MaterialTheme.colorScheme.primary else Color.Gray

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { tugas.imagePath?.let { onPreviewImage(it) } },
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
                        onComplete(isDone)
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

            if (isCompleted && onDelete != null) {
                IconButton(
                    onClick = onDelete,
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

@Composable
fun ImagePreviewDialog(imageUri: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Close")
            }
        },
        text = {
            Image(
                painter = rememberImagePainter(imageUri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
        }
    )
}




