const express = require('express');
const session = require('express-session');

const app = express();

app.set('view engine', 'ejs');
app.set('views', __dirname + '/views');

app.use(express.urlencoded({ extended: true }));
app.use(express.static(__dirname + '/public'));
app.use(
  session({
    secret: 'dev-secret-not-for-production',
    resave: false,
    saveUninitialized: false,
  })
);

// Server-wide value shared by ALL users/sessions - equivalent of the Java
// instance field `infoCompartida` on the Spring controller. It is rendered
// unescaped, so it is a true persisted/stored XSS: whatever the last visitor
// submitted gets executed in every other visitor's browser.
let infoCompartida = null;

app.post('/procesarFormulario', (req, res) => {
  const { info } = req.body;
  req.session.infoUsuario = info;
  infoCompartida = info;
  res.render('resultado_formulario');
});

app.get('/mostrarDatos', (req, res) => {
  res.render('datos', {
    infoUsuario: req.session.infoUsuario,
    infoCompartida,
  });
});

const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`Persisted XSS example listening on http://localhost:${PORT}`);
});
