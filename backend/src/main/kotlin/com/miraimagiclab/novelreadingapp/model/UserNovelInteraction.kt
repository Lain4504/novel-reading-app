package com.miraimagiclab.novelreadingapp.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.FieldType
import java.time.LocalDateTime

@Document(collection = "user_novel_interactions")
data class UserNovelInteraction(
    @Id
    val id: String? = null,

    @Indexed
    @Field(targetType = FieldType.OBJECT_ID)
    val userId: String,

    @Indexed
    @Field(targetType = FieldType.OBJECT_ID)
    val novelId: String,

    // Các trạng thái tương tác
    val hasFollowing: Boolean = false,      // Theo dõi truyện
    val inWishlist: Boolean = false,       // Thêm vào danh sách mong muốn
    val notify: Boolean = false,         // Cho phép nhận thông báo

    // Tiến trình đọc
    val currentChapterNumber: Int? = null, // Số chương đang đọc đến
    @Field(targetType = FieldType.OBJECT_ID)
    val currentChapterId: String? = null, // ID chương hiện tại
    val lastReadAt: LocalDateTime? = null, // Thời gian đọc cuối cùng

    // Thống kê
    val totalChapterReads: Int = 0, // Tổng số lần đọc chương (bao gồm cả đọc lại)

    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)