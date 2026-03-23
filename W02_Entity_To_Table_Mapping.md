# Week 2 - Entity -> Table, ERD, SQL

## Mapping chính (bám theo entity)
- `users` / `roles` / `user_roles`
- `projects` / `project_users`
- `tasks`

## Chuẩn hoá entity -> bảng (theo checklist)
- Quan hệ user-role: `UserEntity.roles` many-to-many qua bảng `user_roles`.
- Quan hệ project-members: `ProjectEntity.members` many-to-many qua bảng `project_users`.
- Quan hệ project-task:
  - `TaskEntity.project` là `ManyToOne` bắt buộc.
  - `TaskEntity.assignedTo` là `ManyToOne` nullable (task có thể chưa được assign).

## Bảng field chi tiết
- Tham chiếu file `W02_Table_Field_Chi_Tiet.md` (liệt kê type + ràng buộc từng cột).

## ERD chi tiết + review quan hệ
- File `ERD.md` mô tả:
  - danh sách entity/tables,
  - các cột quan trọng,
  - flow nghiệp vụ Task (create/assign/update status/list theo user/project).
- Review quan hệ và giải thích tập trung vào các điểm:
  - membership dùng bảng nối để hỗ trợ nhiều user trong 1 project.
  - phân quyền MANAGER/USER map sang rule ở service thay vì nhồi vào entity.

## Fix ERD
- Đã chuẩn hoá lại phần mô tả flow Task để khớp với rule thực thi trong `TaskService` (đặc biệt điều kiện chặn update khi task ở `DONE`).

## SQL tạo bảng + ràng buộc
- File: `db/schema.sql`
  - `CHECK` cho `tasks.status` in (`TODO`,`IN_PROGRESS`,`DONE`)
  - `PK`, `FK`, `INDEX` cho các cột quan hệ (`project_id`, `assigned_user_id`, ...)
  - seed data `>= 30 records` phục vụ test nhanh.

## Seed test data (>= 30 records)
- `db/schema.sql` chèn seed:
  - roles: 2
  - users: 10
  - project_users: 20
  - tasks: 15
  - tổng đủ >= 30 records
  
## Check constraint (DB OK)
- Constraint `tasks.status` được áp dụng bằng `CHECK` trong SQL script.
- Đồng thời trong code, `TaskStatus` được lưu bằng `EnumType.STRING` để giảm sai lệch chuỗi trạng thái.

## Query mẫu
- `db/queries.sql`:
  - query task theo `assigned_user_id`
  - query task theo `project_id`
  - query task theo `status`

## Review & tối ưu query
- Đã tạo các index phục vụ truy vấn:
  - `IX_tasks_project_id`
  - `IX_tasks_assigned_user_id`
  - `IX_project_users_user_id`
- Các query trong `db/queries.sql` bám theo các index và đúng cột dùng trong `TaskRepository` (find theo userId/projectId).

## Lưu ý
- `password_hash` trong `db/schema.sql` đang để placeholder (vì app dùng BCrypt khi login).

