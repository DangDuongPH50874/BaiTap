# Kịch bản demo (Tuần 10) - mô tả theo text để thực hiện live

## Chuẩn bị
- Chạy app theo `application-dev.properties`.
- Mở Swagger UI: `http://localhost:8080/swagger-ui.html` để thao tác nhanh.
- Luôn lưu token:
  - `MANAGER` token để tạo project/task/assign/update status.
  - `USER` token để list task theo quyền.

## Demo 1: Tạo user + login (MANAGER)
1. `POST /api/auth/register`
   - Body (ví dụ):
     ```json
     { "username": "manager_demo", "password": "12345678", "role": "MANAGER" }
     ```
2. `POST /api/auth/login`
   - Body:
     ```json
     { "username": "manager_demo", "password": "12345678" }
     ```
3. Copy token từ response (dùng header `Authorization: Bearer <token>` cho các request sau).

## Demo 2: MANAGER tạo project + thêm member
1. Chuẩn bị vài `USER` (tương tự register/login).
2. `POST /api/projects`
   - Body (ví dụ):
     ```json
     { "name": "ProjectDemo", "description": "demo", "memberUserIds": [2,3] }
     ```
3. Lưu `projectId` từ response.

## Demo 3: Tạo task trong project + assign
1. `POST /api/tasks`
   - Body (ví dụ):
     ```json
     {
       "projectId": 1,
       "title": "T-1",
       "description": "first task",
       "deadline": "2026-04-20",
       "status": "TODO"
     }
     ```
2. `POST /api/tasks/{taskId}/assign`
   - Body:
     ```json
     { "userId": 2 }
     ```

## Demo 4: Update status task (chặn DONE)
1. `PATCH /api/tasks/{taskId}/status`
   - Body: `{ "status": "IN_PROGRESS" }`
2. `PATCH /api/tasks/{taskId}/status`
   - Body: `{ "status": "DONE" }`
3. Thử lại:
   - Body: `{ "status": "IN_PROGRESS" }`
   - Kỳ vọng: nhận lỗi business rule (HTTP 400, message theo `CustomException`).

## Demo 5: USER chỉ xem task của mình
1. Login USER -> lấy token.
2. `GET /api/users/tasks/{id}`
   - Kỳ vọng: trả danh sách task thuộc user đó.
3. `GET /api/projects/tasks/{projectId}`
   - Kỳ vọng:
     - nếu USER là member -> thấy task được assign đúng phạm vi.
     - nếu không phải member -> nhận HTTP 403 (Forbidden).

