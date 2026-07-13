# Ejemplos de vulnerabilidades web (Node.js + Express)

Reimplementaciones en Node/Express de los 6 ejemplos vulnerables de `spring/`. Cada carpeta es un proyecto Express independiente, con su propio `package.json`.

Para la descripción de cada vulnerabilidad y cómo explotarla, consulta el [README común de la raíz del repositorio](../README.md). Este documento solo cubre cómo poner en marcha cada ejemplo.

## Cómo ejecutar cualquier ejemplo

```bash
cd node/<carpeta_del_ejemplo>
npm install
npm start
```

## Puertos y datos de arranque

| Carpeta | Puerto por defecto | Notas |
|---|---|---|
| `security_ejem1_broken_access_control` | 8080 | Usuarios: `michel/pass`, `oscar/pass`, `admin/adminpass` |
| `security_ejem2_sqli` | 8080 | API REST en JSON, `better-sqlite3` en memoria |
| `security_ejem3_xss_reflected` | 8080 | Sin estado |
| `security_ejem4_xss_persisted` | 8080 | Usa `express-session` |
| `security_ejem5_xss_dom` | 8080 | Página estática |
| `security_ejem6_path_traversal` | 9000 | Directorio `images/` con `me.jpg` de ejemplo |

Si quieres tener varios ejemplos levantados a la vez, cambia el puerto con la variable de entorno `PORT`, por ejemplo:

```bash
PORT=8081 npm start
```
