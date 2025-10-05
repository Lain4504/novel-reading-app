package com.miraimagiclab.novelreadingapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.miraimagiclab.novelreadingapp.data.MockData
import com.miraimagiclab.novelreadingapp.ui.components.BookCard
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme

@Composable
fun ExploreScreen(
    onBookClick: (String) -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    fun matchesFilter(book: com.miraimagiclab.novelreadingapp.data.Book): Boolean {
        val filterOk = when (selectedFilter) {
            "All" -> true
            "Novel" -> book.type == com.miraimagiclab.novelreadingapp.data.BookType.NOVEL
            "Light Novel" -> book.type == com.miraimagiclab.novelreadingapp.data.BookType.LIGHT_NOVEL
            "Manga" -> book.type == com.miraimagiclab.novelreadingapp.data.BookType.MANGA
            else -> true
        }
        val queryOk = query.isBlank() || book.title.contains(query, ignoreCase = true) || book.author.contains(query, ignoreCase = true)
        return filterOk && queryOk
    }

    val recommended = remember(query, selectedFilter) {
        MockData.recommendedBooks.filter { matchesFilter(it) }
    }
    val ourPicks = remember(query, selectedFilter) {
        MockData.ourPickBooks.filter { matchesFilter(it) }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search books, authors") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val filters = listOf("All", "Novel", "Light Novel", "Manga")
            filters.forEach { f ->
                val selected = f == selectedFilter
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    tonalElevation = if (selected) 4.dp else 0.dp,
                    modifier = Modifier.clickable { selectedFilter = f }
                ) {
                    Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                        Text(text = f, style = if (selected) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Recommended", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(recommended) { book ->
                BookCard(book = book, onClick = { onBookClick(book.id) })
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(text = "Our Picks", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(ourPicks) { book ->
                BookCard(book = book, onClick = { onBookClick(book.id) })
            }
        }
    }
}
