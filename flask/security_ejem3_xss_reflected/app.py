import os

from flask import Flask, request, send_from_directory

app = Flask(__name__, static_folder="static")


@app.route("/")
def index():
    return send_from_directory(app.static_folder, "index.html")


# Vulnerable: the userName query param is reflected straight into the HTML
# response with no escaping, e.g. /greeting?userName=<script>alert(1)</script>
# Building the response as a plain string (instead of a Jinja2 template)
# means Flask's autoescaping never kicks in.
@app.route("/greeting")
def greeting():
    user_name = request.args.get("userName", "")
    return f"<html><body>\n    <p>Hello, {user_name}</p>\n</body></html>"


if __name__ == "__main__":
    app.run(port=int(os.environ.get("PORT", 8080)), debug=True)
