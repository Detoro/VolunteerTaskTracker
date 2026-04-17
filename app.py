import mysql.connector as connector
from mysql.connector.constants import ClientFlag
from flask import Flask, jsonify, request
import hashlib
from validators import is_valid_email, is_valid_password
from sql_queries import GET_ALL_TASKS, CREATE_TASK, GET_ALL_ORGS, INSERT_ORG, GET_USER_PROFILES, DELETE_TASK, UPDATE_TASK_WITH_TIME, UPDATE_TASK_WITHOUT_TIME, REGISTER_USER, GET_USER_LOGIN, SET_TASK_STATUS_WITH_TIME, SET_TASK_STATUS_WITHOUT_TIME

app = Flask(__name__)

def get_db_connection():
    flags = [ClientFlag.FOUND_ROWS]

    return connector.connect(
        host="localhost",
        user="root",
        password="Put password here",
        database="volunteer_tracker",
        client_flags=flags
    )

@app.route('/api/health', methods=['GET'])
def check_db_health():
    try:
        conn = get_db_connection()
        if conn.is_connected():
            conn.close()
            return jsonify({"status": "SUCCESS", "message": "MySQL is connected perfectly!"}), 200
    except Exception as e:
        return jsonify({"status": "FAILED", "error": str(e)}), 500
    
@app.route('/api/tasks', methods=['GET'])
def get_all_tasks():
    try:
        conn = get_db_connection()
        cursor = conn.cursor(dictionary=True)
        
        cursor.execute(GET_ALL_TASKS)
        raw_tasks = cursor.fetchall()
        
        cursor.close()
        conn.close()
        
        formatted_tasks = []
        for row in raw_tasks:
            task = {
                "id": row["id"],
                "org_id": row["org_id"],
                "title": row["title"],
                "description": row["description"],
                "is_completed": True if row["is_completed"] == 1 else False,
                "assigner": None,
                "assignee": None,
                "start_time": row["start_timestamp"].isoformat() if row["start_timestamp"] else None,
                "end_time": row["end_timestamp"].isoformat() if row["end_timestamp"] else None
            }
            
            if row["assigner_id"]:
                task["assigner"] = {
                    "id": row["assigner_id"],
                    "name": row["assigner_name"],
                    "role": row["assigner_role"]
                }

            if row["assignee_id"]:
                task["assignee"] = {
                    "id": row["assignee_id"],
                    "name": row["assignee_name"],
                    "role": row["assignee_role"]
                }
                
            formatted_tasks.append(task)
            
        print(f"Fetched {len(formatted_tasks)} tasks from the database.")
        return jsonify(formatted_tasks), 200
    except Exception as e:
        print(f"Error fetching tasks: {e}")
        return jsonify({"error": str(e)}), 500

@app.route('/api/tasks/delete', methods=['POST'])
def delete_task():
    try:
        task = request.get_json()
        task_id = task.get('id')
        conn = get_db_connection()
        cursor = conn.cursor(dictionary=True)
        
        cursor.execute(DELETE_TASK, (task_id,))
        conn.commit()
        
        cursor.close()
        conn.close()
        return jsonify({"message": "Task updated successfully!"}), 200
    except Exception as e:
        print(f"Error deleting task: {e}")
        return jsonify({"error": str(e)}), 500

@app.route('/api/tasks/create', methods=['POST'])
def create_task():
    try:
        new_task = request.get_json()
        
        conn = get_db_connection()
        cursor = conn.cursor()
        
        values = (
            new_task['id'], 
            new_task['org_id'],
            new_task['title'], 
            new_task['description'],
            new_task.get('assigner', {}).get('id'),
            new_task.get('assignee', {}).get('id')
        )
        
        cursor.execute(CREATE_TASK, values)
        conn.commit()
        
        cursor.close()
        conn.close()
        
        return jsonify({"message": "Task created successfully in MySQL!"}), 201
    except Exception as e:
        print(f"Error creating task: {e}")
        return jsonify({"error": str(e)}), 500

@app.route('/api/tasks/<task_id>/update', methods=['PUT'])
def update_task_assignee(task_id):
    try:
        update_data = request.get_json()
        assignee_obj = update_data.get('assignee')
        new_assignee_id = assignee_obj.get('id') if assignee_obj else None

        if not new_assignee_id or new_assignee_id.strip() == "":
            new_assignee_id = None
        
        conn = get_db_connection()
        cursor = conn.cursor()
        print(f"DEBUG: Attempting to assign task to User ID: '{new_assignee_id}'")
        sql_query = UPDATE_TASK_WITH_TIME if new_assignee_id else UPDATE_TASK_WITHOUT_TIME

        cursor.execute(sql_query, (new_assignee_id, task_id))

        conn.commit() 
        
        cursor.close()
        conn.close()
        
        return jsonify({"message": "Task assignee updated successfully!"}), 200
    except Exception as e:
        print(f"Error updating task assignee: {e}")
        return jsonify({"error": str(e)}), 500

@app.route('/api/orgs', methods=['GET'])
def get_orgs():
    try:
        conn = get_db_connection()
        cursor = conn.cursor(dictionary=True)
        
        cursor.execute(GET_ALL_ORGS)
        orgs = cursor.fetchall()
        
        cursor.close()
        conn.close()
        print(f"Fetched {len(orgs)} orgs related to from the database.")
        return jsonify(orgs), 200
    except Exception as e:
        print(f"Error fetching orgs: {e}")
        return jsonify({"error": str(e)}), 500

@app.route('/api/users/<org_id>', methods=['GET'])
def get_user_Profiles(org_id):
    try:
        conn = get_db_connection()
        cursor = conn.cursor(dictionary=True)
        
        cursor.execute(GET_USER_PROFILES)
        users = cursor.fetchall()
        
        cursor.close()
        conn.close()
        print(f"Fetched {len(users)} profiles related to {org_id} from the database.")
        return jsonify(users), 200
    except Exception as e:
        print(f"Error fetching user profiles: {e}")
        return jsonify({"error": str(e)}), 500

@app.route('/api/tasks/log', methods=['POST'])
def change_task_status():
    try:
        completed_task = request.get_json()
        task_id = completed_task.get('id')
        is_completed = completed_task.get('is_completed')
        
        conn = get_db_connection()
        cursor = conn.cursor()

        sql_query = SET_TASK_STATUS_WITH_TIME if is_completed else SET_TASK_STATUS_WITHOUT_TIME
        
        cursor.execute(sql_query, (not is_completed, task_id)) 
        
        if cursor.rowcount == 0:
            cursor.close()
            conn.close()
            return jsonify({"error": "Task not found"}), 404
        
        conn.commit()
            
        cursor.close()
        conn.close()
        
        print(f"Success! Task {task_id} marked as {not is_completed} in MySQL.")
        return jsonify({"message": "Task logged successfully!"}), 200
    except Exception as e:
        print(f"Error logging task status: {e}")
        return jsonify({"error": str(e)}), 500

@app.route('/api/users/register', methods=['POST'])
def register_user():
    new_user = request.get_json()
    user_id = new_user.get('id')
    name = new_user.get('name')
    org_id = new_user.get('org_id')
    org_name = new_user.get('org_name', f"{name}'s Organization")
    email = new_user.get('email')
    password = new_user.get('password')
    hashed_password = hashlib.sha256(password.encode('utf-8'))
    role = new_user.get('role', 'SUBSCRIBER')

    if not org_id or org_id.strip() == "":
        org_id = None

    if not (is_valid_email(email) and is_valid_password(password)):
        return jsonify({"error": "Invalid email or password format"}), 400

    conn = get_db_connection()
    cursor = conn.cursor()
    
    try:
        if role == 'ORGANIZATION':
            cursor.execute(INSERT_ORG, (org_id, org_name))
        cursor.execute(REGISTER_USER, (user_id, name, org_id, email, hashed_password.hexdigest(), role))
        conn.commit()
        print(f"New user successfully inserted into MySQL: {name}")
        
        cursor.close()
        conn.close()
        
        return jsonify(new_user), 201
        
    except connector.IntegrityError as e:
        cursor.close()
        conn.close()
        print(f"Integrity error during user registration: {e}")
        return jsonify({"error": str(e)}), 409

@app.route('/api/users/login', methods=['POST'])
def login_user():
    try:
        credentials = request.get_json()
        email = credentials.get('email')
        password = credentials.get('password')
        hashed_password = hashlib.sha256(password.encode('utf-8'))
        
        conn = get_db_connection()
        cursor = conn.cursor(dictionary=True)
        
        cursor.execute(GET_USER_LOGIN, (email,))
        user = cursor.fetchone()

        if not user:
            return jsonify({"error": "User does not exist"}), 404

        user_profile = {
                "id": user["id"],
                "name": user["name"],
                "role": user["role"]
            }
        
        cursor.close()
        conn.close()
        
        if user and user['password'] == hashed_password.hexdigest():
            print(f"Successful login for MySQL user: {user['name']}")
            return jsonify(user_profile), 200
        else:
            print(f"Failed login attempt for email: {email}, {hashed_password.hexdigest()}")
            return jsonify({"error": "Invalid email or password"}), 401
    except Exception as e:
        print(f"Error during user login: {e}")
        return jsonify({"error": str(e)}), 500


# @app.route('/api/groups/<org_id>', methods=['GET'])
# def get_groups(org_id):
#     return jsonify(groups_db), 200

# @app.route('/api/groups/create', methods=['POST'])
# def create_group():
#     new_group = request.get_json()
#     groups_db.append(new_group)
#     print(f"New group created: {new_group.get('name')}")
#     return jsonify({"message": "Group created successfully!"}), 201

# @app.route('/api/groups/delete', methods=['POST'])
# def delete_group():
#     group = request.get_json()
#     group_id = group.get('id')
#     for group in groups_db:
#         if group['id'] == group_id:
#             groups_db.remove(group)
#             return jsonify({"message": "Group deleted successfully!"}), 200
            
#     return jsonify({"error": "Group not found"}), 404
    

if __name__ == '__main__':
    # 0.0.0.0 for phone to connect over Wi-Fi
    app.run(host='0.0.0.0', port=8000, debug=True)