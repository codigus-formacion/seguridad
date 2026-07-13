import os

from flask import Flask, request, render_template, send_from_directory, Response

app = Flask(__name__, static_folder="static")

IMAGES_FOLDER = os.path.join(os.getcwd(), "images")
os.makedirs(IMAGES_FOLDER, exist_ok=True)


@app.route("/")
def index():
    return send_from_directory(app.static_folder, "index.html")


# Vulnerable: Werkzeug does NOT sanitize `file.filename` on its own - that is
# what `werkzeug.utils.secure_filename()` is for, and it is intentionally
# not used here. The filename is joined to IMAGES_FOLDER and saved with no
# containment check, mirroring the Spring original
# (IMAGES_FOLDER.resolve(image.getOriginalFilename())). Intercepting the
# request with a proxy and rewriting the Content-Disposition filename to
# something like "../../../../tmp/evil.txt" writes outside images/.
@app.route("/upload_image", methods=["POST"])
def upload_image():
    image = request.files["image"]
    dest_path = os.path.join(IMAGES_FOLDER, image.filename)
    os.makedirs(os.path.dirname(dest_path), exist_ok=True)
    image.save(dest_path)
    return render_template("uploaded_image.html", image_name=image.filename)


@app.route("/image")
def view_image():
    return render_template("view_image.html", image_name=request.args.get("imageName"))


# Vulnerable: imageName is resolved against IMAGES_FOLDER with no
# sanitization/containment check (no use of Werkzeug's safe_join), so
# "../../../../etc/passwd" escapes the images directory (classic path
# traversal). We read the file manually instead of using
# send_from_directory, which does enforce containment.
@app.route("/download_image")
def download_image():
    image_path = os.path.join(IMAGES_FOLDER, request.args.get("imageName", ""))
    try:
        with open(image_path, "rb") as f:
            data = f.read()
    except OSError:
        return "Not found", 404
    return Response(data, mimetype="image/jpeg")


if __name__ == "__main__":
    app.run(port=int(os.environ.get("PORT", 9000)), debug=True)
