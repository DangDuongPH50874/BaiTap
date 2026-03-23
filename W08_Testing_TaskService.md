# Tuần 8 - Phân tích + unit test TaskService (Mockito) theo checklist

## Mục tiêu theo checklist tuần 8 (items 86-97)
- Phân tích service cần test.
- Viết test cho `TaskService.create`.
- Test rule assign task.
- Test rule update status khi task ở trạng thái DONE.
- Mock repository bằng Mockito.
- Verify behavior bằng `verify()` cho các thao tác quan trọng.
- Bổ sung case lỗi để kiểm tra nhánh failure và nâng độ bao phủ (coverage).
- Review chất lượng test, refactor trùng lặp/naming hàm/biến và hoàn thiện trước khi kết thúc tuần.

## File unit test đã implement
- `src/test/java/com/example/baitap/service/TaskServiceTest.java`

## Các thành phần mock trong test
- `TaskRepository`
- `ProjectRepository`
- `UserRepository`

## Thiết lập SecurityContext cho rule theo role
- Trong test sử dụng `SecurityContextHolder` để set authentication với `UserPrincipal`.
- Helper `setAuth(userId, roleNames)` tạo `UsernamePasswordAuthenticationToken` và set role theo format `ROLE_{ROLE_NAME}`.

## Danh sách test case chính
- `create_shouldCreateTaskWithDefaultTodoStatus`
  - xác nhận khi `status` request null thì hệ thống tạo `TaskStatus.TODO` mặc định.
  - verify:
    - `projectRepository.findById(...)` được gọi.
    - `taskRepository.save(...)` được gọi.
- `assign_shouldFailIfUserNotInProject`
  - xác nhận assign bị chặn khi user không thuộc project chứa task.
  - assert `CustomException` với `ErrorCode.FORBIDDEN`.
  - verify:
    - `taskRepository.findById(...)`, `userRepository.findById(...)` được gọi.
    - không gọi `taskRepository.save(...)` ở nhánh forbidden (`never()`).
- `updateStatus_shouldFailWhenTaskDone`
  - xác nhận chặn update status khi task là `DONE`.
  - assert `CustomException` với `ErrorCode.BUSINESS_RULE`.
  - verify:
    - chỉ gọi `taskRepository.findById(...)`, không gọi `save(...)` trong nhánh fail.
- `update_shouldUpdateTitleDescriptionAndDeadline`
  - xác nhận update task details thay đổi `title`, `description`, `deadline`.
  - verify:
    - `taskRepository.findById(...)` và `taskRepository.save(...)` được gọi.
- `delete_shouldDeleteTask`
  - xác nhận xoá task theo `taskId`.
  - verify:
    - `taskRepository.delete(task)` được gọi và trả đúng `deletedId`.

## Kết quả test (suited theo checklist)
- Kết quả từ `target/surefire-reports/com.example.baitap.service.TaskServiceTest.txt`:
  - `Tests run: 5`
  - `Failures: 0`
  - `Errors: 0`
  - `Skipped: 0`

## Review chất lượng test / refactor
- Đã bổ sung verify() cụ thể để bám checklist “behavior testing”.
- Đã tách helper `setField(...)` để set private fields phục vụ setup entity trong unit tests.
- Dọn dẹp `SecurityContextHolder.clearContext()` ở `@AfterEach` để tránh nhiễu test.

## Kết luận tuần 8
- Bộ test hiện bao phủ đủ các rule chính ở TaskService: create(default TODO), assign(forbidden), updateStatus(DONE business rule), và mở rộng update/delete theo logic service mới.

