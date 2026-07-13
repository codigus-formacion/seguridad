import os

from flask import Flask, render_template, request, redirect, session
from werkzeug.security import check_password_hash

from data import (
    users,
    posts,
    find_user_by_name,
    find_user_by_id,
    find_post_by_id,
    create_post,
    delete_post,
)

app = Flask(__name__)
app.secret_key = "dev-secret-not-for-production"


# Equivalent of the Spring @ModelAttribute that exposes login info to every view.
@app.context_processor
def inject_user():
    user = find_user_by_id(session["user_id"]) if session.get("user_id") else None
    return {
        "logged": bool(user),
        "user_name": user["name"] if user else None,
        "admin": bool(user and "ADMIN" in user["roles"]),
    }


def current_user():
    return find_user_by_id(session["user_id"]) if session.get("user_id") else None


@app.route("/")
def show_posts():
    return render_template("post/list.html", posts=posts)


@app.route("/posts/<int:post_id>")
def show_post(post_id):
    post = find_post_by_id(post_id)
    if not post:
        return "Post not found", 404
    author = find_user_by_id(post["author_id"])
    user = current_user()
    # is_owner only gates whether the Edit/Remove buttons are shown in the
    # view - it is NOT enforced again on the actual edit/remove endpoints.
    is_owner = bool(user) and (user["id"] == post["author_id"] or "ADMIN" in user["roles"])
    return render_template("post/view.html", post={**post, "author": author}, is_owner=is_owner)


@app.route("/newpost", methods=["GET"])
def new_post():
    if not current_user():
        return redirect("/login")
    return render_template("post/new.html")


@app.route("/newpost", methods=["POST"])
def new_post_process():
    user = current_user()
    if not user:
        return redirect("/login")
    post = create_post(request.form["title"], request.form["text"], user["id"])
    return render_template("post/created-message.html", post_id=post["id"])


@app.route("/editpost/<int:post_id>", methods=["GET"])
def edit_post(post_id):
    if not current_user():
        return redirect("/login")
    post = find_post_by_id(post_id)
    if not post:
        return "Post not found", 404
    # No ownership check - any logged-in user can open the edit form for any post.
    return render_template("post/edit.html", post=post)


@app.route("/editpost", methods=["POST"])
def edit_post_process():
    if not current_user():
        return redirect("/login")
    post = find_post_by_id(request.form["id"])
    if not post:
        return "Post not found", 404
    # No ownership check here either - this is the actual vulnerable endpoint.
    post["title"] = request.form["title"]
    post["text"] = request.form["text"]
    return render_template("post/edited-message.html", post_id=post["id"])


@app.route("/removepost/<int:post_id>", methods=["POST"])
def remove_post(post_id):
    if not current_user():
        return redirect("/login")
    post = find_post_by_id(post_id)
    if not post:
        return "Post not found", 404
    # No ownership check - any logged-in user can delete any post.
    delete_post(post["id"])
    return render_template("post/removed-message.html", title=post["title"])


@app.route("/login", methods=["GET"])
def login():
    return render_template("session/login.html")


@app.route("/login", methods=["POST"])
def login_process():
    user = find_user_by_name(request.form.get("username", ""))
    if user and check_password_hash(user["password_hash"], request.form.get("password", "")):
        session["user_id"] = user["id"]
        return redirect("/")
    return redirect("/loginerror")


@app.route("/loginerror")
def login_error():
    return render_template("session/loginerror.html")


@app.route("/logout", methods=["POST"])
def logout():
    session.clear()
    return redirect("/")


if __name__ == "__main__":
    print("Users: michel/pass, oscar/pass, admin/adminpass")
    app.run(port=int(os.environ.get("PORT", 8080)), debug=True)
