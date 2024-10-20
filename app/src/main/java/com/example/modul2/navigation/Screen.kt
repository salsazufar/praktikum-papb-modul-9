package com.example.modul2.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val icon: ImageVector) {
    data object Matkul : Screen("matkul", Icons.Filled.Home)
    data object Tugas : Screen("tugas", Icons.AutoMirrored.Filled.List)
    data object Profile : Screen("profile", Icons.Filled.Person)
}