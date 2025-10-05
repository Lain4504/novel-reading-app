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
            coverUrl = "https://images-cn.ssl-images-amazon.cn/images/I/51VgkCa+sHL.jpg",
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
            coverUrl = "https://images-cn.ssl-images-amazon.cn/images/I/511xm7YWEOL.jpg",
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
            coverUrl = "https://images-cn.ssl-images-amazon.cn/images/I/51dmbPfIumL.jpg",
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
            coverUrl = "https://images-cn.ssl-images-amazon.cn/images/I/51Rw-hU9v3L.jpg",
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
            coverUrl = "https://images-cn.ssl-images-amazon.cn/images/I/51hgjEZEpJL.jpg",
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
            coverUrl = "https://images-cn.ssl-images-amazon.cn/images/I/51HntLYdWhL.jpg",
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
            coverUrl = "https://images-cn.ssl-images-amazon.cn/images/I/51PaQ6PlfGL.jpg",
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
            coverUrl = "https://images-cn.ssl-images-amazon.cn/images/I/51RgyI9DPuL.jpg",
            readTime = "7 hours 15 minutes",
            releaseDate = "2023"
        )
    )
    
    val rankingBooks = listOf(
        Book(
            id = "9",
            title = "Harry Potter and the Philosopher's Stone",
            author = "J.K. Rowling",
            type = BookType.NOVEL,
            genres = listOf("Fantasy", "Adventure"),
            score = 95,
            coverUrl = "https://images-cn.ssl-images-amazon.cn/images/I/51Odx89YOuL.jpg",
            readTime = "8 hours 45 minutes",
            releaseDate = "June 26, 1997"
        ),
        Book(
            id = "10",
            title = "The Lord of the Rings",
            author = "J.R.R. Tolkien",
            type = BookType.NOVEL,
            genres = listOf("Fantasy", "Adventure"),
            score = 94,
            coverUrl = "https://images-cn.ssl-images-amazon.cn/images/I/61GZXfkqtBL.jpg",
            readTime = "15 hours 30 minutes",
            releaseDate = "July 29, 1954"
        ),
        Book(
            id = "11",
            title = "Dune",
            author = "Frank Herbert",
            type = BookType.NOVEL,
            genres = listOf("Sci-Fi", "Fantasy"),
            score = 93,
            coverUrl = "https://images-cn.ssl-images-amazon.cn/images/I/51cp2AVJBkL.jpg",
            readTime = "12 hours 15 minutes",
            releaseDate = "August 1, 1965"
        ),
        Book(
            id = "12",
            title = "1984",
            author = "George Orwell",
            type = BookType.NOVEL,
            genres = listOf("Dystopian", "Political"),
            score = 91,
            coverUrl = "https://images-cn.ssl-images-amazon.cn/images/I/61M6EHkyw5L.jpg",
            readTime = "6 hours 30 minutes",
            releaseDate = "June 8, 1949"
        ),
        Book(
            id = "13",
            title = "Pride and Prejudice",
            author = "Jane Austen",
            type = BookType.NOVEL,
            genres = listOf("Romance", "Classic"),
            score = 90,
            coverUrl = "https://images-cn.ssl-images-amazon.cn/images/I/51aMV3WA4vL.jpg",
            readTime = "9 hours 20 minutes",
            releaseDate = "January 28, 1813"
        ),
        Book(
            id = "14",
            title = "To Kill a Mockingbird",
            author = "Harper Lee",
            type = BookType.NOVEL,
            genres = listOf("Drama", "Classic"),
            score = 89,
            coverUrl = "https://images-cn.ssl-images-amazon.cn/images/I/51k9mfTqPbL.jpg",
            readTime = "7 hours 45 minutes",
            releaseDate = "July 11, 1960"
        ),
        Book(
            id = "15",
            title = "The Great Gatsby",
            author = "F. Scott Fitzgerald",
            type = BookType.NOVEL,
            genres = listOf("Drama", "Classic"),
            score = 87,
            coverUrl = "https://images-cn.ssl-images-amazon.cn/images/I/51JxqXd39UL.jpg",
            readTime = "5 hours 30 minutes",
            releaseDate = "April 10, 1925"
        ),
        Book(
            id = "16",
            title = "One Hundred Years of Solitude",
            author = "Gabriel García Márquez",
            type = BookType.NOVEL,
            genres = listOf("Magical Realism", "Literary"),
            score = 86,
            coverUrl = "https://images-cn.ssl-images-amazon.cn/images/I/51CkgFcmyJL.jpg",
            readTime = "11 hours 45 minutes",
            releaseDate = "May 30, 1967"
        ),
        Book(
            id = "17",
            title = "The Catcher in the Rye",
            author = "J.D. Salinger",
            type = BookType.NOVEL,
            genres = listOf("Coming-of-age", "Literary"),
            score = 85,
            coverUrl = "https://images-cn.ssl-images-amazon.cn/images/I/51BcP+lYZgL.jpg",
            readTime = "6 hours 15 minutes",
            releaseDate = "July 16, 1951"
        ),
        Book(
            id = "18",
            title = "The Hobbit",
            author = "J.R.R. Tolkien",
            type = BookType.NOVEL,
            genres = listOf("Fantasy", "Adventure"),
            score = 84,
            coverUrl = "https://images-cn.ssl-images-amazon.cn/images/I/51GIOECPXjL.jpg",
            readTime = "8 hours 20 minutes",
            releaseDate = "September 21, 1937"
        )
    )
    
    val newBooks = listOf(
        Book(
            id = "19",
            title = "The Silent Patient",
            author = "Alex Michaelides",
            type = BookType.NOVEL,
            genres = listOf("Thriller", "Mystery"),
            score = 88,
            coverUrl = "https://images-cn.ssl-images-amazon.cn/images/I/61PXDQw0NPL.jpg",
            readTime = "6 hours 45 minutes",
            releaseDate = "February 5, 2019"
        ),
        Book(
            id = "20",
            title = "Where the Crawdads Sing",
            author = "Delia Owens",
            type = BookType.NOVEL,
            genres = listOf("Fiction", "Mystery"),
            score = 87,
            coverUrl = "https://images-cn.ssl-images-amazon.cn/images/I/51ktIvhgFWL.jpg",
            readTime = "8 hours 20 minutes",
            releaseDate = "August 14, 2018"
        ),
        Book(
            id = "21",
            title = "The Seven Husbands of Evelyn Hugo",
            author = "Taylor Jenkins Reid",
            type = BookType.NOVEL,
            genres = listOf("Historical Fiction", "Romance"),
            score = 89,
            coverUrl = "https://images-cn.ssl-images-amazon.cn/images/I/51m3GnW0ZSS.jpg",
            readTime = "9 hours 15 minutes",
            releaseDate = "June 13, 2017"
        ),
        Book(
            id = "22",
            title = "Educated",
            author = "Tara Westover",
            type = BookType.NOVEL,
            genres = listOf("Memoir", "Biography"),
            score = 91,
            coverUrl = "https://images-cn.ssl-images-amazon.cn/images/I/512aduAzT9S.jpg",
            readTime = "7 hours 30 minutes",
            releaseDate = "February 20, 2018"
        ),
        Book(
            id = "23",
            title = "The Midnight Library",
            author = "Matt Haig",
            type = BookType.NOVEL,
            genres = listOf("Fiction", "Fantasy"),
            score = 85,
            coverUrl = "https://images-cn.ssl-images-amazon.cn/images/I/51DEpN2al5L.jpg",
            readTime = "6 hours 45 minutes",
            releaseDate = "August 13, 2020"
        )
    )
    
    val completedBooks = listOf(
        Book(
            id = "24",
            title = "The Girl with the Dragon Tattoo",
            author = "Stieg Larsson",
            type = BookType.NOVEL,
            genres = listOf("Crime", "Thriller"),
            score = 86,
            coverUrl = "https://images-cn.ssl-images-amazon.cn/images/I/51tyf5LX9NL.jpg",
            readTime = "10 hours 30 minutes",
            releaseDate = "August 1, 2005"
        ),
        Book(
            id = "25",
            title = "Gone Girl",
            author = "Gillian Flynn",
            type = BookType.NOVEL,
            genres = listOf("Thriller", "Mystery"),
            score = 87,
            coverUrl = "https://images-cn.ssl-images-amazon.cn/images/I/51rajWPOKeL.jpg",
            readTime = "8 hours 45 minutes",
            releaseDate = "June 5, 2012"
        ),
        Book(
            id = "26",
            title = "The Kite Runner",
            author = "Khaled Hosseini",
            type = BookType.NOVEL,
            genres = listOf("Fiction", "Drama"),
            score = 90,
            coverUrl = "https://images-cn.ssl-images-amazon.cn/images/I/51K86R9YOGL.jpg",
            readTime = "9 hours 15 minutes",
            releaseDate = "May 29, 2003"
        ),
        Book(
            id = "27",
            title = "The Help",
            author = "Kathryn Stockett",
            type = BookType.NOVEL,
            genres = listOf("Historical Fiction", "Drama"),
            score = 88,
            coverUrl = "https://images-cn.ssl-images-amazon.cn/images/I/51CK2iaNlUL.jpg",
            readTime = "11 hours 20 minutes",
            releaseDate = "February 10, 2009"
        ),
        Book(
            id = "28",
            title = "Life of Pi",
            author = "Yann Martel",
            type = BookType.NOVEL,
            genres = listOf("Fiction", "Adventure"),
            score = 89,
            coverUrl = "https://images-cn.ssl-images-amazon.cn/images/I/51cmnfQCu2L.jpg",
            readTime = "7 hours 45 minutes",
            releaseDate = "September 11, 2001"
        )
    )
    
    fun getBookDetail(bookId: String): BookDetail? {
        val book = (recommendedBooks + ourPickBooks).find { it.id == bookId }
        return book?.let {
            BookDetail(
                book = it,
                summary = "LIKE LOOKING IN A MIRROR... For better or worse, the Eighty-Sixth Strike Package had become the spearhead of the Republic's war effort. The Legion's advance had been halted, but the cost was high. Shana, Shiden, Theo, Kurena, and the rest of the squadron had survived countless battles, but the war was far from over. As they prepared for their next mission, they couldn't help but wonder what the future held for them and their country. The weight of their responsibilities pressed down on them, but they knew they had to continue fighting for the sake of those they had lost and those they still hoped to protect.",
                chapters = listOf(
                    Chapter("cover", "Eighty Six Vol 9: Valkyrie Has Landed", true, "2023-01-15", ""),
                    Chapter("prologue", "The Beast of Gluttony", true, "2023-01-20", ""),
                    Chapter("chapter1", "The Mermaid's Bargain", true, "2023-01-25", 
                        "CHAPTER 1: THE MERMAID'S BARGAIN\n\n" +
                        "The ocean stretched endlessly before them, its surface reflecting the pale light of the moon like scattered silver coins. Lena stood at the edge of the cliff, her white hair dancing in the night breeze as she gazed down at the tumultuous waters below.\n\n" +
                        "\"Are you sure about this?\" Shin asked, his voice barely audible over the sound of crashing waves. His crimson eyes held a mixture of concern and determination as he watched her from a few steps behind.\n\n" +
                        "Lena didn't turn around. Instead, she reached into her pocket and pulled out a small, ornate seashell. The shell glowed faintly with an otherworldly light, pulsing like a heartbeat.\n\n" +
                        "\"The mermaid's bargain is our only hope,\" she said quietly. \"The Legion's advance cannot be stopped by conventional means. We need their help.\"\n\n" +
                        "Shin's jaw tightened. He had heard the stories—ancient beings who lived in the depths, capable of granting wishes at a terrible price. But the price was always more than anyone could bear.\n\n" +
                        "\"What if the cost is too high?\" he asked, stepping closer to her.\n\n" +
                        "Lena finally turned to face him, her expression resolute. \"Then we'll pay it together. Whatever it takes to protect our people, to end this war once and for all.\"\n\n" +
                        "She raised the shell to her lips and blew into it. The sound that emerged was unlike anything Shin had ever heard—a haunting melody that seemed to echo across the entire ocean, calling to something ancient and powerful.\n\n" +
                        "The waters began to churn, and from the depths emerged a figure that defied description. The mermaid's form was both beautiful and terrifying, her scales shimmering like liquid moonlight, her eyes holding the wisdom of countless centuries.\n\n" +
                        "\"You have called upon the ancient ones,\" the mermaid spoke, her voice like the sound of waves crashing against rocks. \"What do you seek, children of the surface world?\"\n\n" +
                        "Lena stepped forward, her heart pounding in her chest. \"We seek your aid in ending the war that threatens to destroy our world. The Legion must be stopped.\"\n\n" +
                        "The mermaid's eyes narrowed, and for a moment, the ocean itself seemed to hold its breath. \"The price for such aid is great,\" she said slowly. \"Are you prepared to pay it?\"\n\n" +
                        "Shin moved to stand beside Lena, his hand finding hers. \"We are,\" they said together.\n\n" +
                        "The mermaid smiled, and it was a smile that held both promise and warning. \"Then let the bargain be struck. But remember, children—some prices are paid not in gold or silver, but in the very essence of who you are.\"\n\n" +
                        "As the words faded into the night, the mermaid disappeared beneath the waves, leaving behind only the sound of the ocean and the weight of an unspoken promise.\n\n" +
                        "Lena and Shin stood together on the cliff, knowing that their lives had just changed forever. The bargain had been made, and now they would have to live with the consequences—whatever they might be."
                    ),
                    Chapter("interlude", "The King of Spades and Queen of Hearts' Interminable, All Too Trivial Dispute", true, "2023-02-01", ""),
                    Chapter("chapter2", "The Valkyrie's Descent", true, "2023-02-05", ""),
                    Chapter("chapter3", "The Final Battle", true, "2023-02-10", ""),
                    Chapter("epilogue", "A New Beginning", true, "2023-02-15", "")
                ),
                volumes = listOf(
                    Volume("vol1", "Eighty Six Vol 1", "February 10, 2017", "https://images-cn.ssl-images-amazon.cn/images/I/51O9zNg07AL.jpg"),
                    Volume("vol2", "Eighty Six Vol 2", "June 10, 2017", "https://images-cn.ssl-images-amazon.cn/images/I/51nR7EuH1gL.jpg"),
                    Volume("vol3", "Eighty Six Vol 3", "October 10, 2017", "https://images-cn.ssl-images-amazon.cn/images/I/51VgMbrucGL.jpg"),
                    Volume("vol4", "Eighty Six Vol 4", "February 10, 2018", "https://images-cn.ssl-images-amazon.cn/images/I/51SMmys8YGL.jpg"),
                    Volume("vol5", "Eighty Six Vol 5", "June 10, 2018", "https://images-cn.ssl-images-amazon.cn/images/I/51qGXIq3RCL.jpg"),
                    Volume("vol6", "Eighty Six Vol 6", "October 10, 2018", "https://images-cn.ssl-images-amazon.cn/images/I/51b0GZAoEgL.jpg"),
                    Volume("vol7", "Eighty Six Vol 7", "February 10, 2019", "https://images-cn.ssl-images-amazon.cn/images/I/51UtDmaPDNS.jpg"),
                ),
                reviews = listOf(
                    Review("review1", "BookLover123", 5, "Absolutely amazing! The character development and world-building are incredible. This volume really brings the series to new heights.", "2023-01-15"),
                    Review("review2", "AnimeFan99", 4, "Great continuation of the series. The action scenes are well-written and the emotional moments hit hard. Highly recommended!", "2023-01-20"),
                    Review("review3", "LightNovelReader", 5, "Asato Asato never disappoints. The way they handle the themes of war, loss, and hope is masterful. Can't wait for the next volume!", "2023-02-01"),
                    Review("review4", "SciFiEnthusiast", 4, "The mecha battles are intense and the political intrigue keeps you on the edge of your seat. A must-read for sci-fi fans.", "2023-02-10"),
                    Review("review5", "RomanceReader", 3, "While the romance subplot is nice, I wish there was more focus on the relationships between characters. Still a solid read.", "2023-02-15")
                ),
                comments = listOf(
                    Comment("comment1", "NovelReader2023", "This novel is absolutely amazing! The plot twists keep me on the edge of my seat. Can't wait for the next chapter!", "2023-03-01",
                        replies = listOf(
                            Reply("reply1", "EightySixFan", "I know right! The author really knows how to build suspense.", "2023-03-02", "comment1"),
                            Reply("reply2", "PlotTwistLover", "That last chapter was insane! Didn't see that coming at all.", "2023-03-02", "comment1")
                        )
                    ),
                    Comment("comment2", "LightNovelEnthusiast", "The character development in this series is phenomenal. Each character feels so real and relatable.", "2023-03-05",
                        replies = listOf(
                            Reply("reply3", "CharacterFan", "Shin and Lena's relationship development is so well written!", "2023-03-06", "comment2")
                        )
                    ),
                    Comment("comment3", "SciFiReader", "The world-building is incredible. The author has created such a detailed and immersive universe.", "2023-03-08"),
                    Comment("comment4", "MechaLover", "The action scenes are so well described! I can practically see the battles in my mind.", "2023-03-10",
                        replies = listOf(
                            Reply("reply4", "ActionFan", "The mecha designs are so cool! Which one is your favorite?", "2023-03-11", "comment4"),
                            Reply("reply5", "BattleEnthusiast", "The tactical battles are so well thought out. The author really knows military strategy!", "2023-03-12", "comment4")
                        )
                    ),
                    Comment("comment5", "EmotionalReader", "This novel made me cry so many times. The emotional depth is incredible.", "2023-03-15")
                ),
                recommendations = recommendedBooks.take(3)
            )
        }
    }
}
