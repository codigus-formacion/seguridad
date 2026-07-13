const express = require('express');

const app = express();

// The vulnerability here is entirely client-side (reads window.location.hash
// and writes it into innerHTML with no sanitization) - the server only needs
// to serve the static file unmodified.
app.use(express.static(__dirname));

const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`DOM XSS example listening on http://localhost:${PORT}`);
  console.log(`Try: http://localhost:${PORT}/#<img src=x onerror=alert(document.cookie)>`);
});
