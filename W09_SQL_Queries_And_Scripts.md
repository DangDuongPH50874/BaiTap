# Tuần 9 - Dev/Prod profile + Build/run JAR + Swagger + README

## Mục tiêu theo checklist tuần 9
- Tách profile `dev` / `prod`.
- Build JAR và chạy local.
- Fix issue config (nếu có) để môi trường chạy ổn định.
- Tích hợp Swagger UI và document toàn bộ API.
- Test API qua Swagger/Evidence (Postman/Swagger test: OK).
- Chỉnh description API cho “sạch” theo đúng mô hình `ApiResponse`.
- Viết README (setup, run, auth) và hướng dẫn test.
- Nhờ người khác clone và chạy theo README (ghi nhận feedback).

## Tách profile dev/prod
- `application-dev.properties`: cấu hình SQL Server cho môi trường dev.
- `application-prod.properties`: cấu hình SQL Server cho môi trường prod.
- `application-test.properties`: cấu hình H2 in-memory cho unit test.

## Fix issue config (điểm đã ổn định trước khi bước qua Swagger)
- Đảm bảo `app.jwt.secret` và `app.jwt.expiration-minutes` được đọc theo profile (test/dev/prod).
- Kiểm tra `spring.jpa.hibernate.ddl-auto` theo từng profile:
  - test: `create-drop` để unit test có schema.
  - dev/prod: `update/none` tùy cấu hình file properties.

## Tích hợp Swagger UI
- Swagger UI được cấu hình qua `springdoc-openapi-starter-webmvc-ui`.
- URL: `http://localhost:8080/swagger-ui.html`.
- Các endpoint chính đã có tài liệu request/response thông qua DTO và `ApiResponse`.

## Test API qua Swagger (Evidence theo checklist)
- Đã test nhanh các endpoint auth/project/task trực tiếp trên Swagger UI:
  - register/login
  - tạo project
  - tạo task, assign, update status
  - list task theo user/project
- Kết quả: phản hồi đúng format `ApiResponse` và rule validation/business rule hoạt động đúng (Swagger test: OK).

## Clean description API + README
- `README.md` đã có các ví dụ request JSON cho auth/projects/tasks.
- Các endpoint mới theo checklist (update task details + delete task) được thêm vào README để đồng bộ.

## Hướng dẫn test project + ghi nhận feedback
- Đã chuẩn hoá hướng dẫn chạy `README.md`:
  - cấu hình DB,
  - cách mở Swagger,
  - cách gọi API có JWT token (`Authorization: Bearer <token>`).
- Ghi chú để người khác clone repo và chạy theo đúng hướng dẫn (Feedback: OK theo checklist).

