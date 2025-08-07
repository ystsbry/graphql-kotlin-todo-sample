package com.example.graphql

import com.example.model.Todo
import com.example.service.TodoService
import com.expediagroup.graphql.server.operations.Query

class TodoQuery(
    private val todoService: TodoService
) : Query {
    
    fun todos(): List<Todo> = todoService.getAllTodos()
    
    fun todo(id: String): Todo? = todoService.getTodoById(id)
    
    fun todosByStatus(completed: Boolean): List<Todo> = todoService.getTodosByStatus(completed)
}