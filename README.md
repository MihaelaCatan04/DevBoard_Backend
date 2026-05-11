# Warzone - Lab 7 Backend

REST CRUD API for the Lab 6 War Stories application. The API is built with Spring Boot, PostgreSQL, MyBatis, JWT authentication, role-based authorization, pagination, and Swagger UI documentation.

## Technologies

- Java 17
- Spring Boot 4
- Spring Security
- JWT with `jjwt`
- PostgreSQL
- MyBatis
- Swagger UI / OpenAPI via `springdoc-openapi`
- Bucket4j rate limiting

## Main Features

- CRUD operations for posts.
- Comment creation, listing, and deletion.
- Voting for posts.
- JWT authentication with roles stored in the token.
- Token expiration configured to 1 minute for demos.
- Role-based access control:
  - `ADMIN`
  - `WRITER`
  - `VISITOR`
- Pagination with `limit` and `skip`.
- Search, filter, and sort for posts.
- Swagger UI API documentation.
- CORS enabled for a Vite frontend on `http://localhost:5173`.

## Additional Features Implemented

Besides the required Lab 7 CRUD API, the project also includes:

- Full authentication flow with `/auth/register` and `/auth/login`, not only the demo `/token` endpoint.
- Password hashing with BCrypt for registered users.
- Role-based ownership rules: regular writers can update or delete only their own posts and comments, while admins can manage all content.
- Soft delete for posts instead of immediately removing records from the database.
- Admin tools for viewing deleted posts, restoring posts, permanently deleting posts, and managing posts by username.
- Post voting with per-user vote tracking, including vote cancellation and changing vote direction.
- Search, tag filtering, and sorting posts by date, votes, or comment count.
- Global error handling with consistent JSON error responses.
- Request validation for post and comment bodies.
- Rate limiting with Bucket4j to reduce request abuse.
- Docker Compose setup for PostgreSQL, backend, and frontend containers.
- Seed data for demo users, posts, and comments.

## Requirements

- Java 17+
- Maven
- PostgreSQL

## Database Setup

Create a PostgreSQL database named `warzone`:

```sql
CREATE DATABASE warzone;
```

The application runs `schema.sql` and `data.sql` automatically on startup because:

```properties
spring.sql.init.mode=always
```

Seed users are created automatically:

| Username | Password | Role |
| --- | --- | --- |
| `admin` | `password` | `ADMIN` |
| `alice` | `password` | `WRITER` |
| `bob` | `password` | `WRITER` |

## Configuration

The application reads database credentials and JWT secret from environment variables:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/warzone
spring.datasource.username=${username}
spring.datasource.password=${password}
jwt.secret=${jwt}
jwt.expiration=60000
```

Set them before running the app:

```bash
export username=postgres
export password=your_database_password
export jwt=your-very-long-secret-key-at-least-32-characters
```

`jwt.expiration=60000` means tokens expire after 60 seconds.

## Run the Application

```bash
./mvnw spring-boot:run
```

The API runs on:

```text
http://localhost:8080
```

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON is available at:

```text
http://localhost:8080/v3/api-docs
```

## Run with Docker Compose

The project also includes a `docker-compose.yml` file that starts:

- PostgreSQL on port `5432`
- Backend API on port `8080`
- Frontend app on port `5173`

Run:

```bash
docker compose up --build
```

## Authentication

The API has a `/token` endpoint required by the lab. It returns a JWT with the selected role.

```bash
curl -X POST http://localhost:8080/token \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","role":"WRITER"}'
```

Response:

```json
{
  "token": "jwt-token",
  "username": "alice",
  "role": "WRITER"
}
```

Use the token in protected requests:

```bash
Authorization: Bearer jwt-token
```

The project also supports normal authentication:

```http
POST /auth/register
POST /auth/login
```

Example login:

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"password"}'
```

## Authorization Rules

Public endpoints:

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/posts` | List posts |
| `GET` | `/posts/{id}` | Get one post |
| `GET` | `/posts/{postId}/comments` | List comments |
| `POST` | `/token` | Generate demo JWT |
| `POST` | `/auth/register` | Register user |
| `POST` | `/auth/login` | Login user |
| `GET` | `/swagger-ui/**` | Swagger UI |
| `GET` | `/v3/api-docs/**` | OpenAPI docs |

Protected endpoints:

| Method | Endpoint | Required Role |
| --- | --- | --- |
| `POST` | `/posts` | `WRITER` or `ADMIN` |
| `PUT` | `/posts/{id}` | Author or `ADMIN` |
| `DELETE` | `/posts/{id}` | Author or `ADMIN` |
| `POST` | `/posts/{id}/vote?direction=1` | `WRITER` or `ADMIN` |
| `POST` | `/posts/{postId}/comments` | `WRITER` or `ADMIN` |
| `DELETE` | `/posts/{postId}/comments/{commentId}` | Comment author or `ADMIN` |
| `GET` | `/admin/**` | `ADMIN` |
| `DELETE` | `/admin/**` | `ADMIN` |
| `POST` | `/admin/posts/{id}/restore` | `ADMIN` |

## Posts API

### List Posts

```http
GET /posts?limit=10&skip=0
```

Optional query parameters:

| Parameter | Description |
| --- | --- |
| `limit` | Number of posts to return |
| `skip` | Number of posts to skip |
| `tag` | Filter by tag |
| `search` | Search in title and body |
| `sort` | `date`, `votes`, or `comments` |

Example:

```bash
curl "http://localhost:8080/posts?limit=5&skip=0&tag=devops&sort=votes"
```

Response format:

```json
{
  "data": [],
  "limit": 5,
  "skip": 0,
  "total": 20
}
```

### Get One Post

```http
GET /posts/{id}
```

### Create Post

```bash
curl -X POST http://localhost:8080/posts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer jwt-token" \
  -d '{
    "title": "New production story",
    "body": "This is a detailed story about a real development issue.",
    "tag": "backend"
  }'
```

### Update Post

```http
PUT /posts/{id}
```

Only the post author or an `ADMIN` can update a post.

### Delete Post

```http
DELETE /posts/{id}
```

Posts are soft deleted by default.

### Vote on Post

```http
POST /posts/{id}/vote?direction=1
POST /posts/{id}/vote?direction=-1
```

Voting in the same direction again cancels the vote.

## Comments API

### List Comments

```http
GET /posts/{postId}/comments?limit=10&skip=0
```

### Add Comment

```bash
curl -X POST http://localhost:8080/posts/1/comments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer jwt-token" \
  -d '{"body":"This is a comment."}'
```

### Delete Comment

```http
DELETE /posts/{postId}/comments/{commentId}
```

Only the comment author or an `ADMIN` can delete a comment.

## Admin API

All admin endpoints require an `ADMIN` JWT.

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/admin/users/{username}` | Get user by username |
| `GET` | `/admin/users/{username}/posts?limit=10&skip=0` | Get posts by user |
| `DELETE` | `/admin/users/{username}/posts` | Soft delete all posts by user |
| `GET` | `/admin/posts?limit=10&skip=0` | List posts including deleted |
| `POST` | `/admin/posts/{id}/restore` | Restore soft-deleted post |
| `DELETE` | `/admin/posts/{id}/hard` | Permanently delete post |

## Status Codes

The API uses appropriate HTTP status codes:

| Status | Meaning |
| --- | --- |
| `200 OK` | Successful read/update/login/token request |
| `201 Created` | Successful create/register request |
| `204 No Content` | Successful delete |
| `400 Bad Request` | Validation error or invalid input |
| `401 Unauthorized` | Invalid login or expired token |
| `403 Forbidden` | Authenticated user does not have permission |
| `404 Not Found` | Entity not found |
| `409 Conflict` | Username already exists |
| `429 Too Many Requests` | Rate limit exceeded |
| `500 Internal Server Error` | Unexpected server error |

## Frontend Integration

The backend is prepared for integration with the Lab 6 frontend:

- CORS allows requests from `http://localhost:5173`.
- The frontend should store the JWT returned from `/token`, `/auth/login`, or `/auth/register`.
- Protected requests must include:

```http
Authorization: Bearer jwt-token
```

Suggested frontend flow:

1. User logs in or requests a demo token.
2. Frontend stores the token.
3. Frontend sends the token with create, update, delete, vote, and comment requests.
4. When the token expires after 1 minute, the frontend asks the user to log in again or request a new token.

## Lab 7 Requirement Mapping

| Requirement | Implementation |
| --- | --- |
| CRUD API for Lab 6 entities | Posts and comments REST endpoints |
| JWT required for protected operations | Spring Security JWT filter |
| JWT stores role | Token contains `role` claim |
| JWT expiration | `jwt.expiration=60000` |
| `/token` endpoint | `POST /token` |
| Swagger documentation | `/swagger-ui/index.html` |
| Appropriate status codes | Controllers and global exception handler |
| Pagination for large data | `limit` and `skip` on list endpoints |
| Frontend integration | CORS for Vite frontend and JWT authorization flow |
