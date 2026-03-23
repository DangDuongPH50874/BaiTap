-- Create Database BaiTap
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'BaiTap')
BEGIN
    CREATE DATABASE BaiTap;
END
GO

USE BaiTap;
GO

-- BaiTap schema (SQL Server)
-- Tables: users, roles, user_roles, projects, project_users, tasks

IF OBJECT_ID('dbo.user_roles', 'U') IS NOT NULL DROP TABLE dbo.user_roles;
IF OBJECT_ID('dbo.project_users', 'U') IS NOT NULL DROP TABLE dbo.project_users;
IF OBJECT_ID('dbo.tasks', 'U') IS NOT NULL DROP TABLE dbo.tasks;
IF OBJECT_ID('dbo.projects', 'U') IS NOT NULL DROP TABLE dbo.projects;
IF OBJECT_ID('dbo.users', 'U') IS NOT NULL DROP TABLE dbo.users;
IF OBJECT_ID('dbo.roles', 'U') IS NOT NULL DROP TABLE dbo.roles;

CREATE TABLE roles (
    id INT IDENTITY(1,1) NOT NULL,
    name NVARCHAR(32) NOT NULL,
    CONSTRAINT PK_roles PRIMARY KEY (id),
    CONSTRAINT UQ_roles_name UNIQUE (name)
);

CREATE TABLE users (
    id INT IDENTITY(1,1) NOT NULL,
    username NVARCHAR(64) NOT NULL,
    password_hash NVARCHAR(255) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT PK_users PRIMARY KEY (id),
    CONSTRAINT UQ_users_username UNIQUE (username)
);

CREATE TABLE user_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    CONSTRAINT PK_user_roles PRIMARY KEY (user_id, role_id),
    CONSTRAINT FK_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT FK_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE projects (
    id INT IDENTITY(1,1) NOT NULL,
    name NVARCHAR(120) NOT NULL,
    description NVARCHAR(500) NULL,
    manager_id INT NOT NULL,
    CONSTRAINT PK_projects PRIMARY KEY (id),
    CONSTRAINT UQ_projects_name UNIQUE (name),
    CONSTRAINT FK_projects_manager FOREIGN KEY (manager_id) REFERENCES users(id)
);

CREATE TABLE project_users (
    project_id INT NOT NULL,
    user_id INT NOT NULL,
    CONSTRAINT PK_project_users PRIMARY KEY (project_id, user_id),
    CONSTRAINT FK_project_users_project FOREIGN KEY (project_id) REFERENCES projects(id),
    CONSTRAINT FK_project_users_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE tasks (
    id INT IDENTITY(1,1) NOT NULL,
    title NVARCHAR(200) NOT NULL,
    description NVARCHAR(1000) NULL,
    status NVARCHAR(32) NOT NULL,
    deadline DATE NOT NULL,
    project_id INT NOT NULL,
    assigned_user_id INT NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT PK_tasks PRIMARY KEY (id),
    CONSTRAINT FK_tasks_project FOREIGN KEY (project_id) REFERENCES projects(id),
    CONSTRAINT FK_tasks_assigned_user FOREIGN KEY (assigned_user_id) REFERENCES users(id),
    CONSTRAINT CK_tasks_status CHECK (status IN ('TODO','IN_PROGRESS','DONE'))
);

-- Indexes
CREATE INDEX IX_tasks_project_id ON tasks(project_id);
CREATE INDEX IX_tasks_assigned_user_id ON tasks(assigned_user_id);
CREATE INDEX IX_project_users_user_id ON project_users(user_id);

-- =========================
-- Seed test data (>= 30 rows)
-- =========================

-- Roles
INSERT INTO roles(name) VALUES ('USER'), ('MANAGER');

-- Users
-- Lưu ý: `password_hash` trong schema là demo placeholder.
-- Khi dùng thật (login), bạn nên thay bằng BCrypt hash tương ứng với password bạn muốn.
INSERT INTO users(username, password_hash)
VALUES
('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.'),
('manager1', 'bcrypt-hash-demo-1'),
('manager2', 'bcrypt-hash-demo-2'),
('user1', 'bcrypt-hash-demo-3'),
('user2', 'bcrypt-hash-demo-4'),
('user3', 'bcrypt-hash-demo-5'),
('user4', 'bcrypt-hash-demo-6'),
('user5', 'bcrypt-hash-demo-7'),
('user6', 'bcrypt-hash-demo-8'),
('user7', 'bcrypt-hash-demo-9'),
('user8', 'bcrypt-hash-demo-10');

-- Assign roles to users
DECLARE @roleUser INT = (SELECT id FROM roles WHERE name = 'USER');
DECLARE @roleManager INT = (SELECT id FROM roles WHERE name = 'MANAGER');

INSERT INTO user_roles(user_id, role_id)
SELECT u.id, @roleManager FROM users u WHERE u.username IN ('admin','manager1','manager2');

INSERT INTO user_roles(user_id, role_id)
SELECT u.id, @roleUser FROM users u WHERE u.username NOT IN ('admin','manager1','manager2');

-- Projects
INSERT INTO projects(name, description, manager_id)
VALUES
('ProjectA', 'demo A', (SELECT id FROM users WHERE username='manager1')),
('ProjectB', 'demo B', (SELECT id FROM users WHERE username='manager1')),
('ProjectC', 'demo C', (SELECT id FROM users WHERE username='manager2')),
('ProjectD', 'demo D', (SELECT id FROM users WHERE username='manager2')),
('ProjectE', 'demo E', (SELECT id FROM users WHERE username='manager2'));

-- Project membership
DECLARE @pA INT = (SELECT id FROM projects WHERE name='ProjectA');
DECLARE @pB INT = (SELECT id FROM projects WHERE name='ProjectB');
DECLARE @pC INT = (SELECT id FROM projects WHERE name='ProjectC');
DECLARE @pD INT = (SELECT id FROM projects WHERE name='ProjectD');
DECLARE @pE INT = (SELECT id FROM projects WHERE name='ProjectE');

-- ProjectA: manager1 + user1,user2,user3
INSERT INTO project_users(project_id, user_id)
SELECT @pA, id FROM users WHERE username IN ('manager1','user1','user2','user3');

-- ProjectB: manager1 + user4,user5,user6
INSERT INTO project_users(project_id, user_id)
SELECT @pB, id FROM users WHERE username IN ('manager1','user4','user5','user6');

-- ProjectC: manager2 + user1,user5,user7
INSERT INTO project_users(project_id, user_id)
SELECT @pC, id FROM users WHERE username IN ('manager2','user1','user5','user7');

-- ProjectD: manager2 + user2,user4,user8
INSERT INTO project_users(project_id, user_id)
SELECT @pD, id FROM users WHERE username IN ('manager2','user2','user4','user8');

-- ProjectE: manager2 + user3,user6,user7
INSERT INTO project_users(project_id, user_id)
SELECT @pE, id FROM users WHERE username IN ('manager2','user3','user6','user7');

-- Tasks (deadline giả định trong tương lai)
-- ProjectA
INSERT INTO tasks(title, description, status, deadline, project_id, assigned_user_id)
VALUES
('A-1', 't1', 'TODO', DATEADD(DAY, 10, CAST(GETDATE() AS DATE)), @pA, (SELECT id FROM users WHERE username='user1')),
('A-2', 't2', 'IN_PROGRESS', DATEADD(DAY, 12, CAST(GETDATE() AS DATE)), @pA, (SELECT id FROM users WHERE username='user2')),
('A-3', 't3', 'DONE', DATEADD(DAY, 15, CAST(GETDATE() AS DATE)), @pA, (SELECT id FROM users WHERE username='user3'));

-- ProjectB
INSERT INTO tasks(title, description, status, deadline, project_id, assigned_user_id)
VALUES
('B-1', 't1', 'TODO', DATEADD(DAY, 9, CAST(GETDATE() AS DATE)), @pB, (SELECT id FROM users WHERE username='user4')),
('B-2', 't2', 'IN_PROGRESS', DATEADD(DAY, 11, CAST(GETDATE() AS DATE)), @pB, (SELECT id FROM users WHERE username='user5')),
('B-3', 't3', 'DONE', DATEADD(DAY, 14, CAST(GETDATE() AS DATE)), @pB, (SELECT id FROM users WHERE username='user6'));

-- ProjectC
INSERT INTO tasks(title, description, status, deadline, project_id, assigned_user_id)
VALUES
('C-1', 't1', 'TODO', DATEADD(DAY, 8, CAST(GETDATE() AS DATE)), @pC, (SELECT id FROM users WHERE username='user1')),
('C-2', 't2', 'IN_PROGRESS', DATEADD(DAY, 13, CAST(GETDATE() AS DATE)), @pC, (SELECT id FROM users WHERE username='user5')),
('C-3', 't3', 'DONE', DATEADD(DAY, 16, CAST(GETDATE() AS DATE)), @pC, (SELECT id FROM users WHERE username='user7'));

-- ProjectD
INSERT INTO tasks(title, description, status, deadline, project_id, assigned_user_id)
VALUES
('D-1', 't1', 'TODO', DATEADD(DAY, 7, CAST(GETDATE() AS DATE)), @pD, (SELECT id FROM users WHERE username='user2')),
('D-2', 't2', 'IN_PROGRESS', DATEADD(DAY, 10, CAST(GETDATE() AS DATE)), @pD, (SELECT id FROM users WHERE username='user4')),
('D-3', 't3', 'DONE', DATEADD(DAY, 18, CAST(GETDATE() AS DATE)), @pD, (SELECT id FROM users WHERE username='user8'));

-- ProjectE
INSERT INTO tasks(title, description, status, deadline, project_id, assigned_user_id)
VALUES
('E-1', 't1', 'TODO', DATEADD(DAY, 6, CAST(GETDATE() AS DATE)), @pE, (SELECT id FROM users WHERE username='user3')),
('E-2', 't2', 'IN_PROGRESS', DATEADD(DAY, 11, CAST(GETDATE() AS DATE)), @pE, (SELECT id FROM users WHERE username='user6')),
('E-3', 't3', 'DONE', DATEADD(DAY, 17, CAST(GETDATE() AS DATE)), @pE, (SELECT id FROM users WHERE username='user7'));

-- Tổng: roles (2) + users (11) + user_roles (~11) + projects (5) + project_users (20) + tasks (15) >= 30+.

PRINT 'Database BaiTap created successfully with all tables and initial data!';
PRINT 'Default admin user created: username=admin, password=admin123';
PRINT 'IMPORTANT: Change the default admin password in production!';
GO
