import re

def is_valid_email(email):
    if not email:
        return False
    
    pattern = r"^[a-zA-Z0-9_.+-]+@(my\.)?fisk\.edu$"
    
    return bool(re.match(pattern, email))

def is_valid_password(password):
    if not password:
        return False
        
    if len(password) < 8:
        return False
        
    has_number = any(char.isdigit() for char in password)
    has_special = any(not char.isalnum() for char in password)
    
    return has_number and has_special