package com.example

import com.example.graphql.TodoMutation
import com.example.graphql.TodoQuery
import com.example.service.TodoService
import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.toSchema
import graphql.GraphQL
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

@Serializable
data class GraphQLRequest(
    val query: String,
    val operationName: String? = null,
    val variables: Map<String, String>? = null
)

data class GraphQLResponse(
    val data: Map<String, Any?>? = null,
    val errors: List<Map<String, Any>>? = null
)

fun convertToJsonElement(value: Any?): JsonElement {
    return when (value) {
        null -> JsonNull
        is String -> JsonPrimitive(value)
        is Number -> JsonPrimitive(value)
        is Boolean -> JsonPrimitive(value)
        is Map<*, *> -> buildJsonObject {
            value.forEach { (k, v) ->
                if (k is String) {
                    put(k, convertToJsonElement(v))
                }
            }
        }
        is List<*> -> buildJsonArray {
            value.forEach { item ->
                add(convertToJsonElement(item))
            }
        }
        else -> JsonPrimitive(value.toString())
    }
}

fun main() {
    embeddedServer(Netty, port = 8090, host = "0.0.0.0") {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        
        install(CORS) {
            allowMethod(HttpMethod.Options)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Get)
            allowHeader(HttpHeaders.ContentType)
            allowHeader(HttpHeaders.Authorization)
            anyHost()
        }
        
        val todoService = TodoService()
        val config = SchemaGeneratorConfig(supportedPackages = listOf("com.example"))
        
        val queries = listOf(
            TopLevelObject(TodoQuery(todoService))
        )
        
        val mutations = listOf(
            TopLevelObject(TodoMutation(todoService))
        )
        
        val graphQLSchema = toSchema(config, queries, mutations)
        val graphQL = GraphQL.newGraphQL(graphQLSchema).build()
        
        routing {
            get("/") {
                call.respondText("GraphQL Todo API is running! Access /graphql for the endpoint or /playground for GraphQL Playground", ContentType.Text.Plain)
            }
            
            post("/graphql") {
                val request = call.receive<GraphQLRequest>()
                
                val executionInput = graphql.ExecutionInput.newExecutionInput()
                    .query(request.query)
                    .operationName(request.operationName)
                    .variables(request.variables ?: emptyMap())
                    .build()
                
                val executionResult = graphQL.execute(executionInput)
                
                val response = buildJsonObject {
                    put("data", convertToJsonElement(executionResult.getData()))
                    
                    if (executionResult.errors != null && executionResult.errors.isNotEmpty()) {
                        putJsonArray("errors") {
                            executionResult.errors.forEach { error ->
                                addJsonObject {
                                    put("message", error.message ?: "Unknown error")
                                    if (error.locations != null) {
                                        putJsonArray("locations") {
                                            error.locations.forEach { loc ->
                                                addJsonObject {
                                                    put("line", loc.line)
                                                    put("column", loc.column)
                                                }
                                            }
                                        }
                                    }
                                    if (error.path != null) {
                                        putJsonArray("path") {
                                            error.path.forEach { p ->
                                                add(JsonPrimitive(p.toString()))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                call.respondText(response.toString(), ContentType.Application.Json)
            }
            
            get("/playground") {
                call.respondText(graphQLPlaygroundHtml(), ContentType.Text.Html)
            }
        }
    }.start(wait = true)
}

fun graphQLPlaygroundHtml(): String = """
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset=utf-8/>
        <meta name="viewport" content="user-scalable=no, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, minimal-ui">
        <title>GraphQL Playground</title>
        <link rel="stylesheet" href="//cdn.jsdelivr.net/npm/graphql-playground-react/build/static/css/index.css" />
        <link rel="shortcut icon" href="//cdn.jsdelivr.net/npm/graphql-playground-react/build/favicon.png" />
        <script src="//cdn.jsdelivr.net/npm/graphql-playground-react/build/static/js/middleware.js"></script>
    </head>
    <body>
        <div id="root"></div>
        <script>
            window.addEventListener('load', function (event) {
                GraphQLPlayground.init(document.getElementById('root'), {
                    endpoint: '/graphql'
                })
            })
        </script>
    </body>
    </html>
""".trimIndent()