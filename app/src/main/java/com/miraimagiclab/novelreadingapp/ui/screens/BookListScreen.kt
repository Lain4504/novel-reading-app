package com.miraimagiclab.novelreadingapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.miraimagiclab.novelreadingapp.data.MockData
import com.miraimagiclab.novelreadingapp.ui.components.BookCard
import com.miraimagiclab.novelreadingapp.ui.components.StatsCard
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow

@Composable
fun BookListScreen(
    onNavigateInProgress: () -> Unit = {},
    onNavigateCompleted: () -> Unit = {},
    onBookClick: (String) -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val allBooks = remember { MockData.recommendedBooks + MockData.ourPickBooks }

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

    val visibleBooks by remember(query, selectedFilter) {
        mutableStateOf(allBooks.filter { matchesFilter(it) })
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            // Search
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search books, authors") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter
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
                            Text(text = f, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatsCard(
                    icon = Icons.Default.PlayArrow,
                    title = "In Progress",
                    value = "${allBooks.count { !it.isCompleted }}",
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onNavigateInProgress)
                )

                StatsCard(
                    icon = Icons.Default.Check,
                    title = "Completed",
                    value = "${allBooks.count { it.isCompleted }}",
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onNavigateCompleted)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(visibleBooks, key = { it.id }) { book ->
                    BookCard(
                        book = book,
                        onClick = { onBookClick(book.id) }
                    )
                }
            }
        }
    }
}
