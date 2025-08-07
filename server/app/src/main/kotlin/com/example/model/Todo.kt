package com.example.model

import java.util.UUID
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Todo(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String? = null,
    val completed: Boolean = false,
    val createdAt: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
    val updatedAt: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
)