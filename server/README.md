# GraphQL Todo API Server

KotlinとGraphQLを使用したTodoリストAPIサーバーです。

## 機能

- Todo項目のCRUD操作
- GraphQL Query/Mutation
- インメモリデータストレージ（配列ベース）
- GraphQL Playground UI

## セットアップと実行

### 依存関係のインストール
```bash
./gradlew build
```

### サーバーの起動
```bash
./gradlew run
```

サーバーは http://localhost:8080 で起動します。

## エンドポイント

- `/` - ルートエンドポイント（ヘルスチェック）
- `/graphql` - GraphQL APIエンドポイント
- `/playground` - GraphQL Playground UI

## GraphQL スキーマ

### Queries

```graphql
# 全てのTodoを取得
query GetAllTodos {
  todos {
    id
    title
    description
    completed
    createdAt
    updatedAt
  }
}

# IDでTodoを取得
query GetTodoById($id: String!) {
  todo(id: $id) {
    id
    title
    description
    completed
  }
}

# ステータスでTodoをフィルタ
query GetTodosByStatus($completed: Boolean!) {
  todosByStatus(completed: $completed) {
    id
    title
    completed
  }
}
```

### Mutations

```graphql
# 新しいTodoを作成
mutation CreateTodo($title: String!, $description: String) {
  createTodo(title: $title, description: $description) {
    id
    title
    description
    completed
  }
}

# Todoを更新
mutation UpdateTodo($id: String!, $title: String, $description: String, $completed: Boolean) {
  updateTodo(id: $id, title: $title, description: $description, completed: $completed) {
    id
    title
    description
    completed
    updatedAt
  }
}

# Todoを削除
mutation DeleteTodo($id: String!) {
  deleteTodo(id: $id)
}

# Todo完了状態を切り替え
mutation ToggleTodoStatus($id: String!) {
  toggleTodoStatus(id: $id) {
    id
    completed
    updatedAt
  }
}
```

## データモデル

```kotlin
data class Todo(
    val id: String,              // UUID
    val title: String,           // タイトル
    val description: String?,    // 詳細（オプション）
    val completed: Boolean,      // 完了状態
    val createdAt: LocalDateTime,// 作成日時
    val updatedAt: LocalDateTime // 更新日時
)
```

## 技術スタック

- Kotlin
- GraphQL Kotlin
- Ktor（Webフレームワーク）
- Kotlinx Serialization
- Logback（ロギング）