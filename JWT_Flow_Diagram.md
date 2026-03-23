# Sơ đồ flow JWT (tóm tắt)

- `POST /api/auth/login`: client gửi `username/password`, backend xác thực và trả về JWT token.
- Client gọi API protected kèm header `Authorization: Bearer <token>`.
- `JwtAuthenticationFilter`:
  - đọc header,
  - kiểm tra chữ ký token,
  - parse claims (`userId`, `roles`),
  - set `SecurityContextHolder` với `UserPrincipal`.
- Tầng `Service` dùng `SecurityUtils.hasRole` để thực thi đúng rule:
  - `MANAGER` được tạo/assign/update status.
  - `USER` chỉ xem task thuộc phạm vi của mình.

