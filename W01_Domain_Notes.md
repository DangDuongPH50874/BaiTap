# Tuần 1 - Đọc yêu cầu domain & mô hình hoá bài toán

## Mục tiêu theo checklist tuần 1
- Đọc yêu cầu domain và ghi chú nghiệp vụ.
- Xác định entity + field chi tiết (tham chiếu `W02_Table_Field_Chi_Tiet.md`).
- Xác định enum trạng thái `Task`.
- Viết mô tả nghiệp vụ User–Task–Project (tham chiếu `ERD.md`).
- Xây dựng các class entity `User`, `Task`, `Project` (constructor) và định vị chỗ validate phù hợp.
- Refactor OOP, tách trách nhiệm (validation/logic trong đúng tầng).
- Xác định khung logic add/update/delete Task và các case lỗi (null, không hợp lệ, trùng).
- Chạy test console và ghi log output làm bằng chứng chạy được.

## Tóm tắt yêu cầu nghiệp vụ
Hệ thống quản lý `Project` và `Task` theo vai trò:
- `MANAGER` tạo `project`, tạo `task`, gán `task` cho `user`, và cập nhật trạng thái `status`.
- `USER` chỉ xem các `task` của chính mình.

## Enum trạng thái `Task`
`TaskStatus` gồm 3 giá trị:
- `TODO`
- `IN_PROGRESS`
- `DONE`

## Mô tả nghiệp vụ User–Task–Project
Quan hệ và luồng nghiệp vụ được mô tả trong `ERD.md`, trong đó nhấn mạnh:
- `MANAGER` tạo project/task.
- `MANAGER` assign task cho user nếu user thuộc `project_users` của project chứa task.
- Chỉ chặn cập nhật status khi task đang ở `DONE`.
- `USER` lọc danh sách task theo chính user đó.

## Entity và các field cốt lõi
Các entity chính đã được xác định và triển khai:
- `UserEntity` (`src/main/java/.../entity/UserEntity.java`).
- `RoleEntity` (`src/main/java/.../entity/RoleEntity.java`).
- `ProjectEntity` (`src/main/java/.../entity/ProjectEntity.java`).
- `TaskEntity` (`src/main/java/.../entity/TaskEntity.java`).

Chi tiết mapping field-level nằm trong `W02_Table_Field_Chi_Tiet.md` (bảng field chi tiết theo checklist).

## Thiết kế OOP và vị trí validate
Nguyên tắc tách trách nhiệm:
- Entity chỉ lưu trạng thái dữ liệu và cung cấp constructor/setter tối thiểu phục vụ JPA.
- Validate input được thực hiện ở DTO bằng các annotation (`@NotNull`, `@NotBlank`, `@Size`) và custom validator cho `deadline` (`@DeadlineAfterToday`).
- Quy tắc nghiệp vụ (forbidden, not found, conflict, business rule DONE) nằm ở tầng `Service` và ném `CustomException`.

## Thực thi các class entity (constructor)
Một số constructor/khung triển khai đã dùng:
- `UserEntity(String username, String passwordHash)` được dùng cho luồng register.
- `ProjectEntity(String name, String description, UserEntity manager)` được dùng khi tạo project.
- `TaskEntity(String title, String description, TaskStatus status, LocalDate deadline, ProjectEntity project)` được dùng khi tạo task.

Các lỗi “trùng”/“không hợp lệ” được xử lý ở service:
- Trùng `username` khi register: `UserService.register` ném `CustomException(ErrorCode.CONFLICT, ...)`.
- `projectId`/`taskId`/`userId` không tồn tại: ném `ErrorCode.NOT_FOUND`.

## Khung logic add/update/delete Task (định hướng theo checklist)
Sau khi xác định domain, logic task được triển khai theo các hàm trong `TaskService` (phục vụ controller):
- Tạo Task: `POST /api/tasks` -> `TaskService.create`.
- Update thông tin Task: `PUT /api/tasks/{taskId}` -> `TaskService.update`.
- Gán Task cho User: `POST /api/tasks/{taskId}/assign` -> `TaskService.assign`.
- Update status Task: `PATCH /api/tasks/{taskId}/status` -> `TaskService.updateStatus` (chặn khi `DONE`).
- Xoá Task: `DELETE /api/tasks/{taskId}` -> `TaskService.delete`.

## Xử lý lỗi logic (null, trùng, vi phạm business rule)
Các case lỗi đã có hướng xử lý rõ ràng:
- Input null/thiếu trường: bị bắt bởi Bean Validation ở DTO -> trả HTTP 400 qua `GlobalExceptionHandler`.
- Trùng `username` khi register: trả HTTP 409 (Conflict) qua `CustomException`.
- Gán task cho user không thuộc project: trả HTTP 403 (Forbidden).
- Update status khi task là `DONE`: trả HTTP 400 (Business rule).
- Không tìm thấy project/task/user theo ID: trả HTTP 404.

## Test console + ghi log output
- Đã chạy `mvn test` với profile `test` (H2 in-memory) để kiểm chứng build + context load.
- Log console gần nhất được lưu tại `logs/mvn-test-2.log`.

