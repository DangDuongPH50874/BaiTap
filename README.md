# BaiTap (Spring Boot)

## Run

### Database (dev)
Chỉnh các biến môi trường (hoặc sửa trực tiếp `application-dev.properties`):
- `DB_URL` (SQL Server)
- `DB_USERNAME`
- `DB_PASSWORD`

Chạy:
```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

### Build JAR + Run local (theo checklist)
Build:
```powershell
.\mvnw.cmd -DskipTests package
```
Chạy JAR (ví dụ profile `dev`):
```powershell
java -jar .\target\<jar-name>.jar --spring.profiles.active=dev
```

### Swagger
Mở: `http://localhost:8080/swagger-ui.html`

## Auth (JWT)

### Register
`POST /api/auth/register`
```json
{
  "username": "manager1",
  "password": "12345678",
  "role": "MANAGER"
}
```

### Login
`POST /api/auth/login`
```json
{
  "username": "manager1",
  "password": "12345678"
}
```

Response:
```json
{
  "code": 0,
  "message": "OK",
  "data": { "userId": 1, "token": "..." }
}
```

Gọi API có token:
`Authorization: Bearer <token>`

## Projects

### Create project (MANAGER)
`POST /api/projects`
```json
{
  "name": "ProjectA",
  "description": "demo",
  "memberUserIds": [2,3,4]
}
```

## Tasks

### Create task (MANAGER)
`POST /api/tasks`
```json
{
  "projectId": 1,
  "title": "Implement login",
  "description": "JWT + Security",
  "deadline": "2026-04-10",
  "status": "TODO"
}
```

### Assign task (MANAGER)
`POST /api/tasks/{taskId}/assign`
```json
{ "userId": 2 }
```

### Update task status (MANAGER)
`PATCH /api/tasks/{taskId}/status`
```json
{ "status": "IN_PROGRESS" }
```

Rule: không cho update nếu task đã `DONE`.

### Update task details (MANAGER)
`PUT /api/tasks/{taskId}`
```json
{
  "title": "Updated title",
  "description": "Updated description",
  "deadline": "2026-04-20"
}
```

Rule: `deadline` phải lớn hơn current date (custom validation `@DeadlineAfterToday`).

### Delete task (MANAGER)
`DELETE /api/tasks/{taskId}`

### List tasks by user
`GET /api/users/tasks/{id}`

Rule: USER chỉ xem task của chính mình.

### List tasks by project
`GET /api/projects/tasks/{projectId}`

Rule: USER chỉ xem task của chính mình trong các project mình là member.

