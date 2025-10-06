package com.miraimagiclab.novelreadingapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miraimagiclab.novelreadingapp.data.MockData
import com.miraimagiclab.novelreadingapp.ui.components.BookCard
import com.miraimagiclab.novelreadingapp.ui.components.StatsCard

@Composable
fun BookListScreen(
    onBookClick: (String) -> Unit = {},
    onNavigateInProgress: () -> Unit = {},
    onNavigateCompleted: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    // Combine mock lists (an toàn — tránh tham chiếu MockData.books nếu bạn không có)
    val allBooks = remember { MockData.recommendedBooks + MockData.ourPickBooks }

    var query by remember { mutableStateOf("") }
    var showFilterMenu by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }

    // Filter logic (giữ giống Explore)
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

    val filteredBooks = remember(query, selectedFilter) {
        allBooks.filter { matchesFilter(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Top bar (giống Explore)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = "Book List",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Search + Filter row (same UI as Explore)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Search box (custom BasicTextField)
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

            // Filter icon & menu
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
                                    fontWeight = if (selectedFilter == option) FontWeight.Bold else FontWeight.Normal
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

        // Stats row (2 sectors) — keep clickable via Modifier outside StatsCard
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // In Progress Books
            StatsCard(
                icon = Icons.Default.Favorite,
                title = "In Progress",
                value = "${allBooks.count { !it.isCompleted }} Books",
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onNavigateInProgress)
            )

            // Completed Books
            StatsCard(
                icon = Icons.Default.Check,
                title = "Completed Books",
                value = "${allBooks.count { it.isCompleted }} Books",
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onNavigateCompleted)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Book grid (2 columns)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(filteredBooks, key = { it.id }) { book ->
                BookCard(
                    book = book,
                    onClick = { onBookClick(book.id) }
                )
            }
        }
    }
}
