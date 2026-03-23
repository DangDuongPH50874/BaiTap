-- Queries mẫu (SQL Server)

-- 1) Query task theo userId (assigned)
-- @userId = ?
SELECT t.*
FROM tasks t
WHERE t.assigned_user_id = @userId
ORDER BY t.deadline ASC;

-- 2) Query task theo projectId
-- @projectId = ?
SELECT t.*
FROM tasks t
WHERE t.project_id = @projectId
ORDER BY t.deadline ASC;

-- 3) Query task theo status
-- @status = 'TODO' | 'IN_PROGRESS' | 'DONE'
SELECT t.*
FROM tasks t
WHERE t.status = @status
ORDER BY t.deadline ASC;

