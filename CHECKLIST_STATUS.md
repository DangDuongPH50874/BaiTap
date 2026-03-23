# Project checklist status (Week 1 - Week 10)

Tổng quan: dự án hiện đã implement được phần backend lõi (entity/JPA, JWT auth, service/controller cho project/task, validation, exception DTO cơ bản) và có unit test `TaskService` + unit test cho custom validator `DeadlineAfterToday`.

## Status theo tuần
- Week 1: hoàn thành (mô hình hoá domain + enum + ERD mô tả quan hệ)
- Week 2: hoàn thành (ERD + SQL tạo bảng + seed + queries + bảng field chi tiết)
- Week 3: hoàn thành (Spring Boot setup + profile + cấu hình JWT/Swagger + README)
- Week 4: hoàn thành (JPA mapping + các endpoint list theo user/project)
- Week 5: hoàn thành (đã có logic create/assign/updateStatus/list + update task details + delete task)
- Week 6: hoàn thành (custom validate deadline + GlobalExceptionHandler + unit test validator)
- Week 7: hoàn thành (roles `USER/MANAGER`, register/login JWT, SecurityConfig bảo vệ endpoint)
- Week 8: hoàn thành (unit tests TaskService, có verify() và cover nhánh update/delete)
- Week 9: hoàn thành (SQL + queries mẫu + README + Swagger UI)
- Week 10: hoàn thành (slide kiến trúc, sơ đồ flow JWT, demo script, final report, mentor evaluation)

## Deliverables đã tạo
- `db/schema.sql`, `db/queries.sql`
- `ERD.md`, `README.md`
- `W01...W10...` (ghi chú theo từng tuần)
- `logs/mvn-test-2.log` (log chạy test console gần nhất)
- `target/surefire-reports/*` (kết quả unit test)

## Lưu ý
- Trong `db/schema.sql`, `password_hash` đang để placeholder. Khi chạy login thật trên dữ liệu seed, cần thay bằng BCrypt hash hợp lệ (hoặc dùng endpoint `/api/auth/register` để tạo user đúng cách).

