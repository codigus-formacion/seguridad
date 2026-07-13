import os

from flask import Flask, request, session, render_template, send_from_directory

app = Flask(__name__, static_folder="static")
app.secret_key = "dev-secret-not-for-production"

# Server-wide value shared by ALL users/sessions - equivalent of the Java
# instance field `infoCompartida` on the Spring controller. It is rendered
# with the `safe` filter (no escaping), so it is a true persisted/stored
# XSS: whatever the last visitor submitted gets executed in every other
# visitor's browser.
info_compartida = None


@app.route("/")
def index():
    return send_from_directory(app.static_folder, "index.html")


@app.route("/procesarFormulario", methods=["POST"])
def procesar_formulario():
    global info_compartida
    info = request.form.get("info", "")
    session["info_usuario"] = info
    info_compartida = info
    return render_template("resultado_formulario.html")


@app.route("/mostrarDatos")
def mostrar_datos():
    return render_template(
        "datos.html",
        info_usuario=session.get("info_usuario"),
        info_compartida=info_compartida,
    )


if __name__ == "__main__":
    app.run(port=int(os.environ.get("PORT", 8080)), debug=True)
