from flask import Flask, send_from_directory
import os

app = Flask(__name__)

# The vulnerability here is entirely client-side (reads window.location.hash
# and writes it into innerHTML with no sanitization) - the server only needs
# to serve the static file unmodified.
BASE_DIR = os.path.dirname(os.path.abspath(__file__))


@app.route("/")
def index():
    return send_from_directory(BASE_DIR, "index.html")


if __name__ == "__main__":
    app.run(port=int(os.environ.get("PORT", 8080)), debug=True)
