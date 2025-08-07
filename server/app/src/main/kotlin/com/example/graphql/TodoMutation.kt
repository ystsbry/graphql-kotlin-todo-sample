package com.example.graphql

import com.example.model.Todo
import com.example.service.TodoService
import com.expediagroup.graphql.server.operations.Mutation

class TodoMutation(
    private val todoService: TodoService
) : Mutation {
    
    fun createTodo(title: String, description: String? = null): Todo {
        return todoService.createTodo(title, description)
    }
    
    fun updateTodo(id: String, title: String? = null, description: String? = null, completed: Boolean? = null): Todo? {
        return todoService.updateTodo(id, title, description, completed)
    }
    
    fun deleteTodo(id: String): Boolean {
        return todoService.deleteTodo(id)
    }
    
    fun toggleTodoStatus(id: String): Todo? {
        return todoService.toggleTodoStatus(id)
    }
}