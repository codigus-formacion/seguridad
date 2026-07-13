# Ejemplos de vulnerabilidades web (Spring Boot)

6 aplicaciones Spring Boot con vulnerabilidades intencionadas. Cada carpeta es un proyecto Maven independiente, con su propio `pom.xml` y su propio wrapper de Maven (`mvnw` / `mvnw.cmd`), así que no hace falta tener Maven instalado.

Para la descripción de cada vulnerabilidad y cómo explotarla, consulta el [README común de la raíz del repositorio](../README.md). Este documento solo cubre cómo poner en marcha cada ejemplo.

## Cómo ejecutar cualquier ejemplo

Desde la carpeta del ejemplo, usando el wrapper (no requiere Maven instalado, solo JDK):

```bash
cd spring/<carpeta_del_ejemplo>
./mvnw spring-boot:run
```

En Windows, usa `mvnw.cmd spring-boot:run`.

También puedes generar el jar y ejecutarlo directamente:

```bash
./mvnw clean package -DskipTests
java -jar target/*.jar
```

## Puertos y datos de arranque

| Carpeta | Puerto por defecto | Notas |
|---|---|---|
| `security_ejem1_broken_access_control` | **8443 (HTTPS)** | `https://localhost:8443/` — usa un keystore de desarrollo autofirmado (`keystore.jks`), el navegador avisará de certificado no confiable, hay que aceptarlo. Usuarios: `michel/pass`, `oscar/pass`, `admin/adminpass`. Base de datos H2 en memoria, se reinicia con cada arranque. |
| `security_ejem2_sqli` | 8080 | API REST en JSON. Base de datos H2 en memoria. |
| `security_ejem3_xss_reflected` | 8080 | Sin estado. |
| `security_ejem4_xss_persisted` | 8080 | Usa `HttpSession`. |
| `security_ejem5_xss_dom` | — | No es un proyecto Maven (no tiene `pom.xml`), es solo `index.html` estático. Ábrelo directamente en el navegador (`file:///.../index.html`) o sírvelo con cualquier servidor estático, p. ej. `python3 -m http.server` desde esa carpeta. |
| `security_ejem6_path_traversal` | 9000 | Directorio `images/` |

Para cambiar el puerto de un ejemplo concreto sin tocar `application.properties`:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```
