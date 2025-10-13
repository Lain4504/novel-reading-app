package com.miraimagiclab.novelreadingapp.exception

class ImageNotFoundException(id: String) : RuntimeException("Image not found with id: $id")
