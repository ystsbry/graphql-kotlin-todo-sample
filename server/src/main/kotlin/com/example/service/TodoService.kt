package com.example.service

import com.example.model.Todo
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

class TodoService {
    private val todos = ConcurrentHashMap<String, Todo>()
    
    init {
        val sampleTodos = listOf(
            Todo(title = "Learn GraphQL", description = "Study GraphQL basics and best practices"),
            Todo(title = "Build Todo API", description = "Create a GraphQL API for Todo management", completed = true),
            Todo(title = "Write tests", description = "Add unit and integration tests")
        )
        sampleTodos.forEach { todo ->
            todos[todo.id] = todo
        }
    }
    
    fun getAllTodos(): List<Todo> {
        return todos.values.sortedByDescending { it.createdAt }
    }
    
    fun getTodoById(id: String): Todo? {
        return todos[id]
    }
    
    fun getTodosByStatus(completed: Boolean): List<Todo> {
        return todos.values
            .filter { it.completed == completed }
            .sortedByDescending { it.createdAt }
    }
    
    fun createTodo(title: String, description: String?): Todo {
        val todo = Todo(
            title = title,
            description = description
        )
        todos[todo.id] = todo
        return todo
    }
    
    fun updateTodo(id: String, title: String?, description: String?, completed: Boolean?): Todo? {
        val existingTodo = todos[id] ?: return null
        
        val updatedTodo = existingTodo.copy(
            title = title ?: existingTodo.title,
            description = description ?: existingTodo.description,
            completed = completed ?: existingTodo.completed,
            updatedAt = LocalDateTime.now()
        )
        
        todos[id] = updatedTodo
        return updatedTodo
    }
    
    fun deleteTodo(id: String): Boolean {
        return todos.remove(id) != null
    }
    
    fun toggleTodoStatus(id: String): Todo? {
        val existingTodo = todos[id] ?: return null
        
        val updatedTodo = existingTodo.copy(
            completed = !existingTodo.completed,
            updatedAt = LocalDateTime.now()
        )
        
        todos[id] = updatedTodo
        return updatedTodo
    }
}