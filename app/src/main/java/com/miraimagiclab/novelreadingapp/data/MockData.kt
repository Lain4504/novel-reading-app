package com.miraimagiclab.novelreadingapp.data

object MockData {
    
    val userStats = UserStats(
        bookPoints = 1200,
        readBooks = 22
    )
    
    val recommendedBooks = listOf(
        Book(
            id = "1",
            title = "Maze Runner: The Scorch Trials",
            author = "James Dashner",
            type = BookType.NOVEL,
            genres = listOf("Sci-Fi", "Thriller", "Dystopian"),
            score = 85,
            coverUrl = "https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=300&h=400&fit=crop",
            readTime = "8 hours 30 minutes",
            releaseDate = "September 18, 2010"
        ),
        Book(
            id = "2",
            title = "Eighty Six Vol 9: Valkyrie Has Landed",
            author = "Asato Asato",
            series = "Eighty Six",
            type = BookType.LIGHT_NOVEL,
            genres = listOf("Action", "Drama", "Mecha", "Mystery", "Romance", "Sci-Fi", "Supernatural", "Tragedy"),
            score = 86,
            coverUrl = "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=300&h=400&fit=crop",
            readTime = "11 hours 12 minutes",
            releaseDate = "February 22, 2022"
        ),
        Book(
            id = "3",
            title = "The Fragrant Blooms With",
            author = "Unknown Author",
            type = BookType.LIGHT_NOVEL,
            genres = listOf("Romance", "Slice of Life"),
            score = 78,
            coverUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&h=400&fit=crop",
            readTime = "6 hours 45 minutes",
            releaseDate = "March 15, 2023"
        ),
        Book(
            id = "4",
            title = "The Alchemist",
            author = "Michael Scott",
            type = BookType.NOVEL,
            genres = listOf("Fantasy", "Adventure", "Philosophy"),
            score = 92,
            coverUrl = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=300&h=400&fit=crop",
            readTime = "4 hours 20 minutes",
            releaseDate = "1988"
        ),
        Book(
            id = "5",
            title = "Eragon",
            author = "Christopher Paolini",
            type = BookType.NOVEL,
            genres = listOf("Fantasy", "Adventure", "Dragons"),
            score = 88,
            coverUrl = "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=300&h=400&fit=crop",
            readTime = "12 hours 30 minutes",
            releaseDate = "August 26, 2003"
        )
    )
    
    val ourPickBooks = listOf(
        Book(
            id = "6",
            title = "The Alchemist",
            author = "Michael Scott",
            type = BookType.NOVEL,
            genres = listOf("Fantasy", "Adventure"),
            score = 92,
            coverUrl = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=300&h=400&fit=crop",
            readTime = "4 hours 20 minutes",
            releaseDate = "1988"
        ),
        Book(
            id = "7",
            title = "Eragon",
            author = "Christopher Paolini",
            type = BookType.NOVEL,
            genres = listOf("Fantasy", "Adventure"),
            score = 88,
            coverUrl = "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=300&h=400&fit=crop",
            readTime = "12 hours 30 minutes",
            releaseDate = "August 26, 2003"
        ),
        Book(
            id = "8",
            title = "Deeper POI",
            author = "Unknown Author",
            type = BookType.NOVEL,
            genres = listOf("Mystery", "Thriller"),
            score = 75,
            coverUrl = "https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=300&h=400&fit=crop",
            readTime = "7 hours 15 minutes",
            releaseDate = "2023"
        )
    )
    
    fun getBookDetail(bookId: String): BookDetail? {
        val book = (recommendedBooks + ourPickBooks).find { it.id == bookId }
        return book?.let {
            BookDetail(
                book = it,
                summary = "LIKE LOOKING IN A MIRROR... For better or worse, the Eighty-Sixth Strike Package had become the spearhead of the Republic's war effort. The Legion's advance had been halted, but the cost was high. Shana, Shiden, Theo, Kurena, and the rest of the squadron had survived countless battles, but the war was far from over. As they prepared for their next mission, they couldn't help but wonder what the future held for them and their country. The weight of their responsibilities pressed down on them, but they knew they had to continue fighting for the sake of those they had lost and those they still hoped to protect.",
                chapters = listOf(
                    Chapter("cover", "Eighty Six Vol 9: Valkyrie Has Landed"),
                    Chapter("prologue", "The Beast of Gluttony"),
                    Chapter("chapter1", "The Mermaid's Bargain"),
                    Chapter("interlude", "The King of Spades and Queen of Hearts' Interminable, All Too Trivial Dispute"),
                    Chapter("chapter2", "The Valkyrie's Descent"),
                    Chapter("chapter3", "The Final Battle"),
                    Chapter("epilogue", "A New Beginning")
                ),
                volumes = listOf(
                    Volume("vol1", "Eighty Six Vol 1", "February 10, 2017", "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=300&h=400&fit=crop"),
                    Volume("vol2", "Eighty Six Vol 2", "June 10, 2017", "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=300&h=400&fit=crop"),
                    Volume("vol3", "Eighty Six Vol 3", "October 10, 2017", "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=300&h=400&fit=crop"),
                    Volume("vol4", "Eighty Six Vol 4", "February 10, 2018", "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=300&h=400&fit=crop"),
                    Volume("vol5", "Eighty Six Vol 5", "June 10, 2018", "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=300&h=400&fit=crop"),
                    Volume("vol6", "Eighty Six Vol 6", "October 10, 2018", "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=300&h=400&fit=crop"),
                    Volume("vol7", "Eighty Six Vol 7", "February 10, 2019", "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=300&h=400&fit=crop"),
                    Volume("vol8", "Eighty Six Vol 8", "June 10, 2019", "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=300&h=400&fit=crop"),
                    Volume("vol9", "Eighty Six Vol 9", "February 22, 2022", "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=300&h=400&fit=crop")
                ),
                reviews = listOf(
                    Review("review1", "BookLover123", 5, "Absolutely amazing! The character development and world-building are incredible. This volume really brings the series to new heights.", "2023-01-15"),
                    Review("review2", "AnimeFan99", 4, "Great continuation of the series. The action scenes are well-written and the emotional moments hit hard. Highly recommended!", "2023-01-20"),
                    Review("review3", "LightNovelReader", 5, "Asato Asato never disappoints. The way they handle the themes of war, loss, and hope is masterful. Can't wait for the next volume!", "2023-02-01"),
                    Review("review4", "SciFiEnthusiast", 4, "The mecha battles are intense and the political intrigue keeps you on the edge of your seat. A must-read for sci-fi fans.", "2023-02-10"),
                    Review("review5", "RomanceReader", 3, "While the romance subplot is nice, I wish there was more focus on the relationships between characters. Still a solid read.", "2023-02-15")
                ),
                recommendations = recommendedBooks.take(3)
            )
        }
    }
}
