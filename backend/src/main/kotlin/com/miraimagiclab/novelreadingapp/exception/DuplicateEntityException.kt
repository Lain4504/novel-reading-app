package com.miraimagiclab.novelreadingapp.exception

class DuplicateEntityException(message: String? = "Duplicate entity") : ConflictException(message)