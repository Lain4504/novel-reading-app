package com.miraimagiclab.novelreadingapp.exception

class NotificationNotFoundException(id: String) :
    RuntimeException("Notification not found with id: $id")
