# Tuần 5 - Luồng nghiệp vụ Task (status flow, assign, add/update/delete)

## Mục tiêu theo checklist tuần 5 (items 50-61)
- Phân tích rule nghiệp vụ Task (status flow, assign).
- Viết `TaskStatus` (TODO, IN_PROGRESS, DONE).
- Implement API tạo task + validate `projectId` tồn tại.
- Implement API assign task cho user + kiểm tra user thuộc project.
- Implement API update status task + chặn khi task ở DONE.
- Implement API list task theo project và theo user.
- Hoàn thiện logic add/update/delete Task (đúng mục tiêu bài tập) và test full flow bằng Postman/Swagger (Postman test: OK).

## Phân tích rule nghiệp vụ Task
Luồng trạng thái:
- `TODO` -> có thể chuyển sang `IN_PROGRESS`.
- `IN_PROGRESS` -> có thể chuyển sang `DONE`.
- Không cho phép update status khi task đang `DONE`.

Quy tắc assign:
- Chỉ `MANAGER` được assign task.
- User được gán phải là thành viên của project chứa task đó.

## `TaskStatus` (enum)
Định nghĩa tại `src/main/java/.../domain/TaskStatus.java`:
- `TODO`
- `IN_PROGRESS`
- `DONE`

## Implement API tạo Task (Endpoint)
- Endpoint: `POST /api/tasks` -> `TaskController.create` -> `TaskService.create`
- Quy tắc thực thi:
  - Bắt buộc `MANAGER` (kiểm tra bằng `SecurityUtils.hasRole`).
  - `projectId` phải tồn tại (load bằng `projectRepository.findById`, nếu không có trả `NOT_FOUND`).
  - Nếu `status` request null thì default `TODO`.
  - `deadline` đi qua validate tầng DTO (bao gồm custom rule `deadline > current date` ở Week 6).

## Implement API assign Task cho User (Endpoint)
- Endpoint: `POST /api/tasks/{taskId}/assign` -> `TaskService.assign`
- Quy tắc:
  - Bắt buộc `MANAGER`.
  - `taskId` và `userId` phải tồn tại (nếu không có trả `NOT_FOUND`).
  - Kiểm tra membership: `project.getMembers()` phải chứa user được assign.
  - Nếu không thuộc project: ném `CustomException(ErrorCode.FORBIDDEN, ...)`.

## Implement API update status Task (Endpoint)
- Endpoint: `PATCH /api/tasks/{taskId}/status` -> `TaskService.updateStatus`
- Quy tắc:
  - Bắt buộc `MANAGER`.
  - Chặn update khi `task.getStatus() == DONE`:
    - trả `CustomException(ErrorCode.BUSINESS_RULE, "cannot update status when task is DONE")`.

## Implement API list Task theo Project và theo User
- `GET /api/users/tasks/{id}` -> `TaskService.listByUser`
  - `USER`: chỉ xem task của chính họ.
  - `MANAGER`: xem theo userId.
- `GET /api/projects/tasks/{projectId}` -> `TaskService.listByProject`
  - `USER`: chỉ xem task trong project mà họ là member.
  - `MANAGER`: xem toàn bộ task của project.

## Hoàn thiện logic add/update/delete Task (đúng checklist)
- `create`: `POST /api/tasks`.
- `update task details`: `PUT /api/tasks/{taskId}` -> cập nhật `title`, `description`, `deadline`.
- `assign`: `POST /api/tasks/{taskId}/assign`.
- `update task status`: `PATCH /api/tasks/{taskId}/status` (có business rule chặn DONE).
- `delete`: `DELETE /api/tasks/{taskId}`.

## Test full flow bằng Postman/Evidence - Result
- Đã kiểm thử tuần tự theo flow: `register/login` -> tạo `project` -> tạo `task` -> assign -> update status -> list theo user/project.
- Kết quả: các response đều trả về `ApiResponse` chuẩn và rule phân quyền/business rule hoạt động đúng (Postman test: OK).

## Fix bug + ổn định code
- Đã xử lý các lỗi phát sinh trong quá trình build/ràng buộc (ví dụ compile thiếu getter ở `TaskEntity` và các chỉnh sửa để luồng service hoạt động ổn định với validation + exception).

