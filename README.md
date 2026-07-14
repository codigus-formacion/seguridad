# Seguridad Web - Ejemplos y ejercicios de vulnerabilidades

Ejemplos didácticos de aplicaciones web con vulnerabilidades intencionadas, reimplementados en distintas tecnologías (`spring/`, `node/`, `flask/`). Los 6 ejemplos son equivalentes entre carpetas: mismas vulnerabilidades, mismas rutas/parámetros en la medida de lo posible.

Para instrucciones de instalación y puertos de cada stack, consulta el README de la carpeta correspondiente (p. ej. [node/README.md](node/README.md)). Este documento describe, de forma común a todas las implementaciones, en qué consiste cada vulnerabilidad y cómo explotarla. Sustituye `<PUERTO>` por el puerto real indicado en el README de la implementación que estés usando.

---

## Ejercicios simples propuestos

Para cada ejemplo de vulnerabilidad, se propone al alumno:
- Lanzar la aplicación web y comprobar que funciona
- Reproducir la vulnerabilidad siguiendo las instrucciones de explotación indicadas en este documento
- Proponer una solución para mitigar la vulnerabilidad con cambios en el código de la aplicación

---

## 1. `security_ejem1_broken_access_control` — IDOR / Broken Access Control

**Usuarios de prueba:** `michel/pass`, `oscar/pass`, `admin/adminpass`.

**Vulnerabilidad:** los endpoints de editar/borrar un post (`POST /editpost`, `GET /editpost/:id`, `POST /removepost/:id`) solo comprueban que haya un usuario logueado, pero nunca comprueban que ese usuario sea el autor del post (ni admin). El botón "Edit/Remove" se oculta en la vista si no eres el dueño, pero eso es un control únicamente de interfaz — el endpoint sigue aceptando la petición igualmente.

**Cómo explotarla (desde el navegador, con las herramientas de desarrollador):**
1. Entra en `http://localhost:<PUERTO>/` y comprueba que el post "First post" (id `1`) es de `michel`, mientras que "Second post" (id `2`) es de `oscar`.
2. Inicia sesión como `oscar` desde el formulario superior.
3. Abre el post `2` (el que sí es de `oscar`) para que aparezcan los botones/formularios de Edit y Remove — para el post `1` (ajeno) no se muestran, porque la vista los oculta si no eres el dueño.
4. Abre las herramientas de desarrollador del navegador (F12 → pestaña "Elements"/"Inspector") y localiza:
   - El formulario de borrado: `<form action="/removepost/2" method="post">`. Edita el atributo `action` y cambia el `2` por `1`.
   - O el formulario de edición: el enlace "Edit" lleva a `/editpost/2`; puedes editar directamente ese `href` en el DOM y cambiarlo a `/editpost/1`, o bien, ya dentro del formulario de edición, cambiar el valor del campo oculto `<input type="hidden" name="id" value="2">` por `1`.
5. Pulsa el botón "Remove" (o "Save Changes" si fuiste por la vía de edición). La petición sale del propio formulario de la página, con las cookies de sesión de `oscar` ya puestas por el navegador.
6. El post `1`, de `michel`, se borra/edita aunque lo haya ejecutado `oscar` — el servidor solo comprueba que haya sesión iniciada, nunca que el `id` corresponda a un post propio.

---

## 2. `security_ejem2_sqli` — Inyección SQL

API REST en JSON, sin login.

**Vulnerabilidad:** `GET /api/books/` construye la consulta SQL concatenando directamente el parámetro `lang` (y `from`/`to`) sin parametrizar.

**Cómo explotarla (desde el navegador):** al ser una petición `GET`, basta con pegar la URL en la barra de direcciones — el propio navegador codifica automáticamente los espacios y comillas al pulsar Enter. Ataque destructivo con sentencias apiladas (borra toda la tabla de libros):
```
http://localhost:<PUERTO>/api/books/?from=1900&to=2000&lang=es' or '1' = '1'; DELETE FROM book  --
```
Tras cargar esa URL, vuelve a entrar en `http://localhost:<PUERTO>/api/books/` (sin nada más): la respuesta es una lista vacía, la tabla ha sido borrada.

También puede hacerse con curl:
```bash
curl -G "http://localhost:<PUERTO>/api/books/" \
  --data-urlencode "from=1900" --data-urlencode "to=2000" \
  --data-urlencode "lang=es' or '1' = '1'; DELETE FROM book  --"
```

---

## 3. `security_ejem3_xss_reflected` — XSS Reflejado

**Vulnerabilidad:** `GET /greeting` vuelca el parámetro `userName` directamente en el HTML de respuesta sin escapar.

**Cómo explotarla:**
- Desde el navegador:
  ```
  http://localhost:<PUERTO>/greeting?userName=<script>alert(document.cookie)</script>
  ```
- O con curl, para ver el HTML reflejado sin escapar:
  ```bash
  curl -G "http://localhost:<PUERTO>/greeting" --data-urlencode "userName=<script>alert(1)</script>"
  ```

---

## 4. `security_ejem4_xss_persisted` — XSS Persistente (almacenada)

**Vulnerabilidad:** `POST /procesarFormulario` guarda el campo `info` en dos sitios: una variable por sesión (`infoUsuario`) y una variable compartida por **todos** los usuarios del servidor (`infoCompartida`). `GET /mostrarDatos` escapa `infoUsuario` al renderizarlo, pero **no** escapa `infoCompartida`.

**Cómo explotarla:**
1. Envía un payload en el campo compartido:
   ```bash
   curl -X POST http://localhost:<PUERTO>/procesarFormulario \
     --data-urlencode 'info=<script>alert(document.cookie)</script>'
   ```
2. Visita `http://localhost:<PUERTO>/mostrarDatos` (desde el mismo navegador o desde cualquier otro, incluso sin haber enviado nada): el script se ejecuta para **cualquier** visitante, porque `infoCompartida` es una variable global de servidor, no de sesión — esa es la diferencia entre XSS reflejado y XSS persistente/almacenado.

---

## 5. `security_ejem5_xss_dom` — XSS basado en DOM

Página estática; la vulnerabilidad es 100% cliente (nunca viaja al servidor).

**Vulnerabilidad:** el script de `index.html` lee `window.location.hash`, lo decodifica y lo inyecta en `innerHTML` sin sanitizar.

**Cómo explotarla:** abre en el navegador (el fragmento `#...` no se envía al servidor, por lo que ni siquiera queda registrado en logs):
```
http://localhost:<PUERTO>/#<img src=x onerror=alert(document.cookie)>
```

---

## 6. `security_ejem6_path_traversal` — Path Traversal (lectura y escritura arbitraria)

Directorio de imágenes: `images/`

**Vulnerabilidad — lectura (LFI):** `GET /download_image?imageName=...` resuelve la ruta uniendo `imageName` al directorio de imágenes sin comprobar que el resultado siga dentro de `images/`.

**Cómo explotarla (lectura):**
```bash
curl "http://localhost:<PUERTO>/download_image?imageName=../../../../../../../../../../etc/hostname"
```
Esto devuelve el contenido de `/etc/hostname` (o cualquier fichero legible por el proceso), fuera del directorio `images/`. El número de `../` necesarios depende de la profundidad absoluta de la carpeta del proyecto en tu disco; si no funciona a la primera, añade más `../`.

**Vulnerabilidad — escritura (subida arbitraria):** el endpoint de subida (`POST /upload_image`) usa el nombre de fichero recibido en la petición (`Content-Disposition: filename=...`) sin sanear, y lo une al directorio de imágenes de la misma forma insegura que la lectura.

**Cómo explotarla (escritura):** un formulario HTML normal solo permite elegir el fichero (el navegador envía el nombre base, sin rutas), así que hay que **interceptar la petición** con un proxy (Burp, ZAP, mitmproxy...) o construirla a mano, y modificar el `filename` del `Content-Disposition` del `multipart/form-data` para incluir una ruta traversal. Con curl se puede simular la intercepción indicando el `filename` directamente:

Una subida normal desde el navegador (sin manipular la petición) sigue guardando el fichero dentro de `images/` con normalidad.
