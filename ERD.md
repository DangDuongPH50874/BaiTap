# ERD (tóm tắt)

## Entities

- `users (id, username, password_hash, created_at)`
- `roles (id, name)` với `name` thuộc: `USER`, `MANAGER`
- `user_roles (user_id, role_id)` : many-to-many `users` <-> `roles`
- `projects (id, name, description, manager_id)` : `manager_id` là `users.id`
- `project_users (project_id, user_id)` : many-to-many membership `projects` <-> `users`
- `tasks (id, title, description, status, deadline, project_id, assigned_user_id)`
  - `project_id` -> `projects.id`
  - `assigned_user_id` (nullable) -> `users.id`

## Flow nghiệp vụ (Task)

- `MANAGER` tạo `project`
- `MANAGER` tạo `task` cho `project`
- `MANAGER` assign task cho `user` nếu `user` nằm trong `project_users` của project đó
- `MANAGER` update status:
  - không cho update nếu task đang ở `DONE`

## Phân quyền

- `MANAGER` tạo project/task/assign/update status
- `USER` chỉ xem task của chính họ qua:
  - `GET /api/users/tasks/{id}`
  - `GET /api/projects/tasks/{projectId}` (lọc theo assigned user)

