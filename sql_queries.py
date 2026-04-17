GET_ALL_TASKS = """SELECT 
    t.id, t.org_id, t.title, t.description, t.is_completed, t.start_timestamp, t.end_timestamp,
    
    -- The Assigner (Alias u1)
    u1.id AS assigner_id, 
    u1.name AS assigner_name, 
    u1.role AS assigner_role,
    
    -- The Assignee (Alias u2)
    u2.id AS assignee_id, 
    u2.name AS assignee_name, 
    u2.role AS assignee_role

    FROM tasks t
    LEFT JOIN users u1 ON t.assigner_id = u1.id
    LEFT JOIN users u2 ON t.assignee_id = u2.id;"""

SET_TASK_STATUS_WITH_TIME = "UPDATE tasks SET is_completed = %s, end_timestamp = CURRENT_TIMESTAMP WHERE id = %s"
SET_TASK_STATUS_WITHOUT_TIME = "UPDATE tasks SET is_completed = %s, end_timestamp = NULL WHERE id = %s"
GET_COMPLETED_TASKS = "SELECT * FROM tasks WHERE is_completed = TRUE"
CREATE_TASK = """
        INSERT INTO tasks (id, org_id, title, description, assigner_id, assignee_id)
        VALUES (%s, %s, %s, %s, %s, %s)
    """
DELETE_TASK = "DELETE FROM tasks WHERE id = %s"
UPDATE_TASK_WITH_TIME = "UPDATE tasks SET assignee_id = %s, start_timestamp = CURRENT_TIMESTAMP WHERE id = %s"
UPDATE_TASK_WITHOUT_TIME = "UPDATE tasks SET assignee_id = %s, start_timestamp = NULL WHERE id = %s"
GET_ALL_ORGS = "SELECT * FROM organizations"
GET_USER_PROFILES = "SELECT * FROM users"
REGISTER_USER = """
        INSERT INTO users (id, name, org_id, email, password, role) 
        VALUES (%s, %s, %s, %s, %s, %s)
    """
GET_USER_LOGIN = "SELECT * FROM users WHERE email = %s"
INSERT_ORG = "INSERT INTO organizations (id, name) VALUES (%s, %s)"