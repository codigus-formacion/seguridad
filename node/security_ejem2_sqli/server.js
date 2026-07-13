const express = require('express');
const db = require('./db');

const app = express();

function isNotEmptyField(value) {
  return value !== undefined && value !== null && value !== '';
}

// Vulnerable: builds the SQL query by string concatenation, just like the
// original Spring `BookService.findAll()`. The `lang` query param (and to a
// lesser extent `from`/`to`) is injected straight into the SQL string with no
// parameterization/escaping.
//
// Example exploit:
//   /api/books/?lang=es' or '1'='1
function findAll(from, to, lang) {
  let query = 'SELECT * FROM book';
  if ((from && to) || isNotEmptyField(lang)) query += ' WHERE';
  if (from && to) query += ` publication_year BETWEEN ${from} AND ${to}`;
  if (from && to && isNotEmptyField(lang)) query += ' AND';
  if (isNotEmptyField(lang)) query += ` lang='${lang}'`;
  return db.prepare(query).all();
}

app.get('/api/books/', (req, res) => {
  const { from, to, lang } = req.query;
  try {
    const books = findAll(from, to, lang);
    res.json(books);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.get('/api/books/:id', (req, res) => {
  const book = db.prepare('SELECT * FROM book WHERE id = ?').get(req.params.id);
  if (!book) return res.status(404).json({ error: 'Book not found' });
  res.json(book);
});

const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`SQL Injection example listening on http://localhost:${PORT}`);
  console.log(`Try: http://localhost:${PORT}/api/books/?lang=es' or '1'='1`);
});
