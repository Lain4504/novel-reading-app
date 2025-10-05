package com.miraimagiclab.novelreadingapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.miraimagiclab.novelreadingapp.data.BookType
import com.miraimagiclab.novelreadingapp.data.MockData
import com.miraimagiclab.novelreadingapp.ui.components.BookCard

@Composable
fun CompletedBookScreen(
    onBookClick: (String) -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    // Lấy tất cả sách đã hoàn thành
    val allCompleted = remember {
        (MockData.recommendedBooks + MockData.ourPickBooks).filter { it.isCompleted }
    }

    // Lọc theo filter + query
    fun matchesFilter(book: com.miraimagiclab.novelreadingapp.data.Book): Boolean {
        val filterOk = when (selectedFilter) {
            "All" -> true
            "Novel" -> book.type == BookType.NOVEL
            "Light Novel" -> book.type == BookType.LIGHT_NOVEL
            "Manga" -> book.type == BookType.MANGA
            else -> true
        }
        val queryOk = query.isBlank() ||
                book.title.contains(query, ignoreCase = true) ||
                book.author.contains(query, ignoreCase = true)
        return filterOk && queryOk
    }

    val visibleBooks by remember(query, selectedFilter) {
        mutableStateOf(allCompleted.filter { matchesFilter(it) })
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            Text("Completed Books", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(12.dp))

            // Search bar
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search completed books") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter chips
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

            // Hiển thị grid sách
            if (visibleBooks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("You haven't completed any books yet", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(visibleBooks, key = { it.id }) { book ->
                        BookCard(book = book, onClick = { onBookClick(book.id) })
                    }
                }
            }
        }
    }
}
