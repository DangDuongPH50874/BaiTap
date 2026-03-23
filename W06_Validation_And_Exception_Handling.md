# Tuần 6 - Validate input + CustomException + GlobalExceptionHandler

## Mục tiêu theo checklist tuần 6 (items 62-73)
- Xác định input cần validate ở các endpoint liên quan Task.
- Thêm validate cơ bản cho Task (`@NotBlank`, `@Size`).
- Validate `deadline > current date` bằng custom rule.
- Tạo `CustomException` và `GlobalExceptionHandler`.
- Mapping exception -> HTTP code đúng theo `ErrorCode`.
- Standard hoá `ApiResponse` (code/message/data) và regression test.

## Xác định input cần validate
Các input chính liên quan Task gồm:
- `POST /api/tasks` -> DTO `CreateTaskRequest`.
- `PUT /api/tasks/{taskId}` -> DTO `UpdateTaskRequest`.

Các trường validate theo checklist:
- `title`: `@NotBlank`, `@Size(min=3, max=200)`.
- `description`: `@Size(max=1000)`.
- `deadline`: `@NotNull` + custom rule `@DeadlineAfterToday`.
- Các input còn lại (ví dụ `projectId`, `taskId`, `userId`) xử lý theo hướng:
  - validate cơ bản bằng DTO/Bean Validation nếu có annotation.
  - kiểm tra tồn tại và rule nghiệp vụ ở tầng `Service` (throw `CustomException` tương ứng).

## Validate `deadline` > current date (custom rule)
- Annotation: `validation/DeadlineAfterToday`.
- Validator: `DeadlineAfterTodayValidator`.
- Quy tắc thực thi:
  - Nếu `value == null`: trả `true` để `@NotNull` xử lý lỗi riêng.
  - Nếu không null: `value.isAfter(LocalDate.now())` mới hợp lệ.

## Test input sai (Evidence theo checklist)
- Đã kiểm thử các tình huống sai input:
  - `deadline` nhỏ hơn hoặc bằng ngày hiện tại.
  - `title` rỗng/không đạt độ dài.
- Kết quả: hệ thống trả HTTP 400 với message validate đúng qua `GlobalExceptionHandler` (Postman test: OK).

## Tạo `CustomException` + `ErrorCode`
- `CustomException` giữ `ErrorCode` để trả đúng HTTP code.
- `ErrorCode` quy định mapping:
  - 400 (validation/business rule)
  - 401 (unauthorized)
  - 403 (forbidden)
  - 404 (not found)
  - 409 (conflict)

## Viết `GlobalExceptionHandler` (mapping exception -> HTTP code)
- `MethodArgumentNotValidException` -> HTTP 400 + ghép message field errors.
- `CustomException` -> HTTP theo `ErrorCode` và format `ApiResponse.error`.
- `Exception` -> HTTP 500 và message chung để tránh lộ chi tiết nội bộ.

## Chuẩn hoá `ApiResponse`
- `ApiResponse<T>` có 3 thành phần: `code`, `message`, `data`.
- Endpoint trả về:
  - thành công: `ApiResponse.ok(data)` (code = 0, message = "OK")
  - lỗi: `ApiResponse.error(code, message)`

## Regression test (OK)
- Đã bổ sung unit test cho validator `DeadlineAfterToday`:
  - `src/test/java/.../validation/DeadlineAfterTodayValidatorTest.java`.
- Chạy `mvn test` ở profile `test` để đảm bảo các phần validate + service + handler hoạt động ổn định.

## Ghi chú
- Các bài test “400/404/500” và mapping exception -> HTTP code được kiểm theo luồng API bằng Postman/Swagger (Postman test: OK theo yêu cầu checklist).

