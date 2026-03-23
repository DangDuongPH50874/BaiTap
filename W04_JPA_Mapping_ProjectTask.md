# Tuần 4 - Mapping JPA ProjectEntity/TaskEntity + triển khai API list task

## Mục tiêu theo checklist tuần 4 (items 38-49)
- Mapping `ProjectEntity` và `TaskEntity`.
- Fix lazy/eager để tránh lỗi nạp quan hệ không cần thiết.
- Xác nhận `App OK` và `Test mapping OK` (bằng chứng chạy test/context).
- Viết `TaskRepository`, `ProjectService`, `TaskService`.
- Viết `TaskController` và API list task theo user/project.
- Fix bug JPA phát sinh trong quá trình compile/chạy.

## Mapping quan hệ (bám theo entity)
### `ProjectEntity`
- `manager`: `@ManyToOne(fetch = LAZY, optional = false)` với `manager_id`.
- `members`: `@ManyToMany` qua bảng nối `project_users`.

### `TaskEntity`
- `project`: `@ManyToOne(fetch = LAZY, optional = false)` với `project_id`.
- `assignedTo`: `@ManyToOne(fetch = LAZY, optional = true)` với `assigned_user_id`.
- `status`: `@Enumerated(EnumType.STRING)` đảm bảo đúng 3 trạng thái `TODO/IN_PROGRESS/DONE`.

## Fix lazy/eager và đảm bảo App chạy OK
- Định hướng dùng `fetch = LAZY` cho các quan hệ nhiều-nhiều/nhúng để hạn chế tải dữ liệu thừa và tránh vòng tham chiếu.
- Ở tầng `Service`, các response không trả toàn bộ entity liên quan; thay vào đó ánh xạ ra `TaskResponse` chỉ gồm các ID cần thiết (ví dụ `assignedUserId`, `projectId`), giảm rủi ro lazy-loading trong response.

## Test mapping OK (bằng chứng)
- Đã chạy `mvn test` với profile `test` (H2 in-memory) để đảm bảo:
  - Spring Data JPA scan thành công repository.
  - Hibernate khởi tạo `EntityManagerFactory` thành công.
  - Unit tests chạy pass (đặc biệt là `TaskServiceTest`).

## Viết `TaskRepository`, `ProjectService`, `TaskService`
- `TaskRepository` có các query theo checklist:
  - `findByAssignedTo_Id(Long userId)`.
  - `findByProject_Id(Long projectId)`.
- `ProjectService` triển khai `createProject`:
  - kiểm tra `MANAGER` qua `SecurityUtils.hasRole`.
  - kiểm tra unique `project.name` thông qua `projectRepository.existsByName`.
  - tạo `ProjectEntity`, thêm `manager` vào `members`, sau đó thêm các `memberUserIds` nếu request cung cấp.
- `TaskService` triển khai các hàm nền tảng:
  - `create`, `assign`, `updateStatus`, và các hàm list theo user/project phục vụ API ở bước tiếp theo.
  - hiện thực rule `USER chỉ xem task của chính họ` trong `listByUser`/`listByProject`.

## Viết `TaskController` và API list task OK
Các endpoint list task đã triển khai:
- `GET /api/users/tasks/{id}` -> gọi `TaskService.listByUser`.
- `GET /api/projects/tasks/{projectId}` -> gọi `TaskService.listByProject`.

## Fix bug JPA phát sinh trong quá trình triển khai
- Đã xử lý lỗi compile khi `TaskService` cần getter `TaskEntity.getDescription()`; bổ sung getter để build ổn định.

## Ghi chú
- Các thay đổi đã được xác nhận thông qua `mvn test` (profile `test`) và unit tests hiện tại chạy pass.

