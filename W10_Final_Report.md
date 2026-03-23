# Bài tổng kết (Tuần 10)

Trong quá trình thực hiện bài tập, em đã hệ thống hoá được mô hình `User–Project–Task` và triển khai đầy đủ các phần:

## Những phần đã học được
- Cách thiết kế entity và quan hệ JPA để phục vụ bài toán many-to-many (membership) và assigned relation.
- Cách chuẩn hoá API response qua `ApiResponse` và xử lý lỗi tập trung bằng `GlobalExceptionHandler`.
- Xây dựng phân quyền theo role dùng JWT:
  - `JwtUtil` sinh/validate token
  - `JwtAuthenticationFilter` gắn thông tin user/role vào `SecurityContext`
  - tầng `Service` kiểm soát rule `MANAGER` vs `USER`
- Áp dụng validate đầu vào cho nghiệp vụ `deadline` thông qua custom annotation `@DeadlineAfterToday`.

## Kết quả
- Đã triển khai các endpoint chính: auth, projects, tasks (create/assign/update status/list/update/delete).
- Đã có unit tests cho `TaskService` và unit test cho validator `deadline`.

## Fix issue cuối
- Đã hoàn thiện các phần nghiệp vụ còn thiếu để đồng bộ với checklist, gồm:
  - update task details (`PUT /api/tasks/{taskId}`)
  - delete task (`DELETE /api/tasks/{taskId}`)
- Đã cập nhật unit tests để cover thêm các nhánh update/delete và bổ sung `verify()` cho hành vi quan trọng.

## Tổng kết học được gì
- Cách chia tầng để code rõ ràng: validation ở DTO, nghiệp vụ ở Service, trả lỗi tập trung qua GlobalExceptionHandler.
- Cách kết hợp JWT (Filter + SecurityContext) với rule ở tầng service theo role.
- Cách dùng Mockito để kiểm thử business rule độc lập với DB.

