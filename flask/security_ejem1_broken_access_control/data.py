from werkzeug.security import generate_password_hash

users = [
    {"id": 1, "name": "michel", "password_hash": generate_password_hash("pass"), "roles": ["USER"]},
    {"id": 2, "name": "oscar", "password_hash": generate_password_hash("pass"), "roles": ["USER"]},
    {"id": 3, "name": "admin", "password_hash": generate_password_hash("adminpass"), "roles": ["USER", "ADMIN"]},
]

posts = [
    {"id": 1, "title": "First post", "text": "This is the content of the first post", "author_id": 1},
    {"id": 2, "title": "Second post", "text": "This is the content of the second post", "author_id": 2},
    {"id": 3, "title": "Third post", "text": "This is the content of the third post", "author_id": 1},
]

_next_post_id = 4


def find_user_by_name(name):
    return next((u for u in users if u["name"] == name), None)


def find_user_by_id(user_id):
    return next((u for u in users if u["id"] == user_id), None)


def find_post_by_id(post_id):
    return next((p for p in posts if p["id"] == int(post_id)), None)


def create_post(title, text, author_id):
    global _next_post_id
    post = {"id": _next_post_id, "title": title, "text": text, "author_id": author_id}
    posts.append(post)
    _next_post_id += 1
    return post


def delete_post(post_id):
    post = find_post_by_id(post_id)
    if post:
        posts.remove(post)
