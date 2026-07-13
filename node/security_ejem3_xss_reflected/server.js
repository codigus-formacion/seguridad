const express = require('express');

const app = express();

app.use(express.static(__dirname + '/public'));

// Vulnerable: the userName query param is reflected straight into the HTML
// response with no escaping, e.g. /greeting?userName=<script>alert(document.cookie)</script>
app.get('/greeting', (req, res) => {
  const userName = req.query.userName;
  res.send(`<html><body>\n    <p>Hello, ${userName}</p>\n</body></html>`);
});

const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`Reflected XSS example listening on http://localhost:${PORT}`);
  console.log(`Try it out: http://localhost:${PORT}/greeting?userName=<script>alert(document.cookie)</script>`);
});
