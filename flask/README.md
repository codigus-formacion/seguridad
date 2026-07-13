# Ejemplos de vulnerabilidades web (Python + Flask)

Reimplementaciones en Python/Flask de los 6 ejemplos vulnerables de `spring/` (y `node/`). Cada carpeta es una aplicación Flask independiente (un único `app.py` por ejemplo), pero todas comparten el mismo entorno virtual y el mismo `requirements.txt` en la raíz de `flask/` — solo dependen de Flask, así que no hace falta un venv por ejemplo.

Para la descripción de cada vulnerabilidad y cómo explotarla, consulta el [README común de la raíz del repositorio](../README.md). Este documento solo cubre cómo poner en marcha cada ejemplo.

## Cómo ejecutar cualquier ejemplo

Crea el entorno virtual **una sola vez**, desde la raíz de `flask/`:

```bash
cd flask
python3 -m venv venv
source venv/bin/activate   # en Windows: venv\Scripts\activate
pip install -r requirements.txt
```

A partir de ahí, para lanzar cualquier ejemplo basta con activar ese mismo venv (si no lo está ya) y ejecutar su `app.py`:

```bash
cd flask/<carpeta_del_ejemplo>
python app.py
```

## Puertos y datos de arranque

| Carpeta | Puerto por defecto | Notas |
|---|---|---|
| `security_ejem1_broken_access_control` | 8080 | Usuarios: `michel/pass`, `oscar/pass`, `admin/adminpass`. Datos en memoria, se reinician con cada arranque. |
| `security_ejem2_sqli` | 8080 | API REST en JSON. SQLite en memoria (módulo `sqlite3` de la librería estándar). |
| `security_ejem3_xss_reflected` | 8080 | Sin estado. |
| `security_ejem4_xss_persisted` | 8080 | Usa la sesión de Flask (cookie firmada). |
| `security_ejem5_xss_dom` | 8080 | Página estática; la vulnerabilidad es enteramente cliente. |
| `security_ejem6_path_traversal` | 9000 | Directorio `images/` con `me.jpg` de ejemplo ya incluido. |

Para cambiar el puerto de un ejemplo concreto, exporta la variable de entorno `PORT` antes de arrancar, o edita la llamada `app.run(port=...)` al final de `app.py`.
