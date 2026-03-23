# Tuần 3 - Init Spring Boot + tầng User (Repository/Service/Controller)

## Mục tiêu theo checklist tuần 3 (items 26-37)
- Khởi tạo project Spring Boot và chuẩn hoá cấu trúc package.
- Cấu hình DB + profile và kiểm chứng log startup chạy OK.
- Viết `UserEntity`, `UserRepository`, `UserService`, `UserController`.
- Test API User bằng Postman/Swagger (Postman test: OK).
- Xử lý lỗi cơ bản và refactor naming cho đúng request DTO.
- Chuẩn bị ghi chú/đồng bộ cách gọi API theo format đã thống nhất.

## Khởi tạo project + chuẩn hoá package
Các package chính được tách theo vai trò:
- `entity`: `UserEntity`, `RoleEntity`, `ProjectEntity`, `TaskEntity`.
- `domain`: `TaskStatus`, `RoleName`.
- `repository`: `UserRepository`, `RoleRepository`, `ProjectRepository`, `TaskRepository`.
- `service`: `UserService`, `ProjectService`, `TaskService`.
- `controller`: `UserController`, `ProjectController`, `TaskController`.
- `security`: `JwtUtil`, `JwtAuthenticationFilter`, `SecurityUtils`, `SecurityConfig`, `UserPrincipal`.
- `exception`: `CustomException`, `GlobalExceptionHandler`, `ErrorCode`.
- `validation`: rule `DeadlineAfterToday`.
- `dto`: DTO request/response cho Auth/Project/Task.

## Cấu hình DB và profile chạy được
- `application.properties`: bật profile `dev` mặc định, cấu hình cổng `server.port`, cấu hình JWT (secret/expiration) và swagger path.
- `application-dev.properties`: cấu hình SQL Server cho môi trường dev (dùng placeholder).
- `application-test.properties`: cấu hình H2 in-memory cho unit test (H2Dialect, `ddl-auto=create-drop`).
- `application-prod.properties`: cấu hình SQL Server prod.

## Kiểm tra log startup OK
- Đã chạy `mvn test` với profile `test` để đảm bảo Spring context + JPA khởi tạo thành công (bằng chứng gồm log Hibernate/Hikari/H2).
- Log console được lưu tại `logs/mvn-test-2.log`.

## Viết `UserEntity` + `UserRepository`
### `UserEntity`
Triển khai tại `src/main/java/.../entity/UserEntity.java` với các field chính:
- `id`, `username` (unique)
- `passwordHash`
- `createdAt`
- quan hệ `roles` (many-to-many qua `user_roles`)

Constructor dùng cho luồng register:
- `UserEntity(String username, String passwordHash)`.

### `UserRepository`
Triển khai tại `src/main/java/.../repository/UserRepository.java`:
- `findByUsername`
- `existsByUsername`

## Viết `UserService` (logic register/login)
Triển khai tại `src/main/java/.../service/UserService.java`:
- `register`:
  - kiểm tra trùng `username` bằng `existsByUsername`
  - chọn `role` (default `USER` nếu request không cung cấp)
  - hash password bằng `BCryptPasswordEncoder`
  - lưu user và role vào DB
- `login`:
  - xác thực `username/password` bằng `passwordEncoder.matches`
  - sinh authorities từ role theo format `ROLE_{ROLE_NAME}`
  - tạo JWT token bằng `JwtUtil.generateToken`

## Viết `UserController` (API Auth)
Triển khai tại `src/main/java/.../controller/UserController.java`:
- `POST /api/auth/register`
- `POST /api/auth/login`

## Test API User (Postman) - Result
- Đã test 2 endpoint auth qua Postman/Swagger: `POST /api/auth/register`, `POST /api/auth/login` (Postman test: OK).
- Kết quả trả về đúng cấu trúc `ApiResponse` (code/message/data).
- Các request invalid được trả lỗi theo `GlobalExceptionHandler` (400 validation).

## Xử lý lỗi cơ bản
Các lỗi cơ bản được chuẩn hoá theo `ErrorCode`:
- 400: lỗi validate request (`MethodArgumentNotValidException`).
- 401: sai credentials (`CustomException(ErrorCode.UNAUTHORIZED, ...)`).
- 409: trùng `username` (`CustomException(ErrorCode.CONFLICT, ...)`).

## Refactor naming / JSON deserialization
- Bổ sung setter cho các DTO request (`RegisterRequest`, `LoginRequest`) để đảm bảo JSON mapping hoạt động ổn định (tránh lỗi deserialization).

## Ghi chú
- Phần Swagger UI: dùng `http://localhost:8080/swagger-ui.html` để kiểm tra request/response nhanh.
- Phần README: có hướng dẫn auth theo format chung để hỗ trợ test theo checklist.

