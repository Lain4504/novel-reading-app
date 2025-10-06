package com.miraimagiclab.novelreadingapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miraimagiclab.novelreadingapp.data.MockData
import com.miraimagiclab.novelreadingapp.ui.components.BookCard
import androidx.compose.ui.draw.clip


@Composable
fun ExploreScreen(
    onBookClick: (String) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    var showFilterMenu by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }

    // Filter logic
    fun matchesFilter(book: com.miraimagiclab.novelreadingapp.data.Book): Boolean {
        val filterOk = when (selectedFilter) {
            "All" -> true
            "Novel" -> book.type == com.miraimagiclab.novelreadingapp.data.BookType.NOVEL
            "Light Novel" -> book.type == com.miraimagiclab.novelreadingapp.data.BookType.LIGHT_NOVEL
            "Manga" -> book.type == com.miraimagiclab.novelreadingapp.data.BookType.MANGA
            else -> true
        }
        val queryOk = query.isBlank() ||
                book.title.contains(query, ignoreCase = true) ||
                book.author.contains(query, ignoreCase = true)
        return filterOk && queryOk
    }

    val recommended = remember(query, selectedFilter) {
        MockData.recommendedBooks.filter { matchesFilter(it) }
    }
    val ourPicks = remember(query, selectedFilter) {
        MockData.ourPickBooks.filter { matchesFilter(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {

        // 🔙 Top bar with Back + Title + Trash Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // cho 2 phần ở 2 đầu
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "Explore Books",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            // Trash Button
            IconButton(onClick = { /* TODO: handle delete action */ }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = androidx.compose.ui.graphics.Color(0xFF118B50) // xanh lá chuẩn Material
                )
            }

        }

        Spacer(modifier = Modifier.height(8.dp))

        // 🔍 Search + Filter Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Custom Search Box
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(45.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search icon",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = query,
                        onValueChange = { query = it },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        decorationBox = { innerTextField ->
                            if (query.isEmpty()) {
                                Text(
                                    "Search...",
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                            innerTextField()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Filter button
            Box {
                IconButton(onClick = { showFilterMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = "Filter",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                DropdownMenu(
                    expanded = showFilterMenu,
                    onDismissRequest = { showFilterMenu = false }
                ) {
                    listOf("All", "Novel", "Light Novel", "Manga").forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    option,
                                    fontWeight = if (selectedFilter == option)
                                        FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                selectedFilter = option
                                showFilterMenu = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔸 Recommended Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recommended for you",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = "See all",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.clickable { /* TODO: navigate to see all recommended */ }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(recommended) { book ->
                BookCard(book = book, onClick = { onBookClick(book.id) })
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 🔸 Our Picks Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Our Picks",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = "See all",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.clickable { /* TODO: navigate to see all our picks */ }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(ourPicks) { book ->
                BookCard(book = book, onClick = { onBookClick(book.id) })
            }
        }
    }
}
