package com.miraimagiclab.novelreadingapp.exception

class FileSizeExceededException(
    message: String = "File size exceeds the maximum allowed limit of 5MB"
) : RuntimeException(message)
