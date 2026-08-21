package com.desponsor.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

private data class BottomTab(
    val route: AppRoute,
    val label: String,
)

private val bottomTabs =
    listOf(
        BottomTab(AppRoute.Home, "Home"),
        BottomTab(AppRoute.Explore, "Explore"),
        BottomTab(AppRoute.Settings, "Settings"),
    )

/** Reaches Home, Explore, and Settings from anywhere in one tap (SC-001). */
@Composable
fun DeSponsorBottomBar(
    current: AppRoute?,
    onSelect: (AppRoute) -> Unit,
) {
    NavigationBar {
        bottomTabs.forEach { tab ->
            NavigationBarItem(
                selected = current == tab.route,
                onClick = { onSelect(tab.route) },
                icon = { Icon(tab.route.icon(), contentDescription = tab.label) },
                label = { Text(tab.label) },
            )
        }
    }
}

private fun AppRoute.icon() =
    when (this) {
        AppRoute.Home -> Icons.Filled.Home
        AppRoute.Explore -> Icons.Filled.Explore
        AppRoute.Settings -> Icons.Filled.Settings
        else -> Icons.Filled.Home
    }
