# Bảng field chi tiết (Entity -> thuộc tính)

## `users`
| Field | Type | Ràng buộc/ghi chú |
|---|---|---|
| `id` | `BIGINT/INT` | PK, auto increment |
| `username` | `VARCHAR(64)` | `NOT NULL`, `UNIQUE` |
| `password_hash` | `VARCHAR(255)` | `NOT NULL` (BCrypt hash) |
| `created_at` | `DATETIME2` | `NOT NULL` default `SYSUTCDATETIME()` |

## `roles`
| Field | Type | Ràng buộc/ghi chú |
|---|---|---|
| `id` | `BIGINT/INT` | PK, auto increment |
| `name` | `NVARCHAR(32)` | `NOT NULL`, `UNIQUE` (`USER`, `MANAGER`) |

## `user_roles`
| Field | Type | Ràng buộc/ghi chú |
|---|---|---|
| `user_id` | `INT` | PK 1 phần, FK -> `users(id)` |
| `role_id` | `INT` | PK 1 phần, FK -> `roles(id)` |

## `projects`
| Field | Type | Ràng buộc/ghi chú |
|---|---|---|
| `id` | `BIGINT/INT` | PK, auto increment |
| `name` | `NVARCHAR(120)` | `NOT NULL`, `UNIQUE` |
| `description` | `NVARCHAR(500)` | NULL |
| `manager_id` | `INT` | `NOT NULL`, FK -> `users(id)` |

## `project_users`
| Field | Type | Ràng buộc/ghi chú |
|---|---|---|
| `project_id` | `INT` | PK 1 phần, FK -> `projects(id)` |
| `user_id` | `INT` | PK 1 phần, FK -> `users(id)` |

## `tasks`
| Field | Type | Ràng buộc/ghi chú |
|---|---|---|
| `id` | `BIGINT/INT` | PK, auto increment |
| `title` | `NVARCHAR(200)` | `NOT NULL` |
| `description` | `NVARCHAR(1000)` | NULL |
| `status` | `NVARCHAR(32)` | `NOT NULL`, `CHECK` (`TODO`, `IN_PROGRESS`, `DONE`) |
| `deadline` | `DATE` | `NOT NULL` |
| `project_id` | `INT` | `NOT NULL`, FK -> `projects(id)` |
| `assigned_user_id` | `INT` | NULL, FK -> `users(id)` |

