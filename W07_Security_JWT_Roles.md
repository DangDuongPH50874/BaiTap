# Tuần 7 - Thiết kế Role & JWT SecurityConfig

## Mục tiêu theo checklist tuần 7 (items 74-85)
- Thiết kế bảng Role & UserRole (ERD + implement).
- Thêm role `USER`, `MANAGER`.
- Làm API `Register` + BCrypt hash password.
- Làm API `Login` trả JWT (JwtUtil).
- Làm JWT Filter + SecurityConfig bảo vệ endpoint.
- Kiểm thử bằng token thật (Postman/Swagger test: OK).

## Thiết kế bảng Role & UserRole
- Entity/ERD được mô tả trong `ERD.md`.
- Triển khai bằng:
  - `RoleEntity` (bảng `roles`), field `name` thuộc `RoleName` (`USER`, `MANAGER`).
  - `UserEntity` quan hệ `roles` many-to-many qua bảng `user_roles`.
- Seed role tự động:
  - `startup/DataInitializer` đảm bảo `roles` có đủ `USER` và `MANAGER`.

## API Register
- Endpoint: `POST /api/auth/register` (trong `UserController`).
- DTO: `RegisterRequest`.
- Rule:
  - Validate cơ bản bằng Bean Validation (`@NotBlank`, `@Size`).
  - Default role = `USER` nếu request không gửi `role`.
  - Nếu `username` đã tồn tại -> throw `CustomException(ErrorCode.CONFLICT)`.

## Hash password bằng BCrypt
- `UserService.register` dùng `BCryptPasswordEncoder` để mã hoá `password` -> `passwordHash`.

## API Login JWT
- Endpoint: `POST /api/auth/login` (trong `UserController`).
- Flow:
  - Kiểm tra `username/password` qua `passwordEncoder.matches`.
  - Tạo `authorities` theo role: `ROLE_{roleName}`.
  - `JwtUtil.generateToken` trả JWT gồm:
    - `userId` claim
    - `roles` claim
    - `subject` là username

## JWT Filter + SecurityContext
- Filter: `security/JwtAuthenticationFilter`.
- Cách hoạt động:
  - Đọc header `Authorization`.
  - Kiểm tra JWT `Bearer ...`.
  - Parse claims -> tạo `UserPrincipal` -> set vào `SecurityContextHolder`.

## SecurityConfig bảo vệ endpoint
- `SecurityConfig` cấu hình:
  - PermitAll: `/api/auth/**`, `/swagger-ui.html`, `/swagger-ui/**`, `/v3/api-docs/**`.
  - Các endpoint còn lại yêu cầu authenticated.

## Phân quyền theo checklist (MANAGER vs USER)
- Thực thi ở tầng `Service` bằng `SecurityUtils.hasRole`:
  - `MANAGER` được phép `create project/task`, `assign task`, `update status`.
  - `USER` chỉ truy xuất task thuộc phạm vi của mình (rule lọc theo userId và membership project).

## Test bằng token thật (Evidence theo checklist)
- Đã kiểm thử luồng:
  - Register -> Login -> Copy token.
  - Gọi các API protected bằng header `Authorization: Bearer <token>`.
- Kỳ vọng:
  - Thiếu token hoặc token sai -> HTTP 401/UNAUTHORIZED (Postman test: OK).
  - Dùng role không đúng -> HTTP 403/FORBIDDEN đúng nghiệp vụ (Postman test: OK).

## Ghi chú
- Mặc dù phân quyền chi tiết theo role nằm trong service checks (bám checklist rule), SecurityConfig vẫn đảm bảo endpoint yêu cầu đăng nhập (authenticated).

