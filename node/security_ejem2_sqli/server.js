const express = require('express');
const db = require('./db');

const app = express();

function isNotEmptyField(value) {
  return value !== undefined && value !== null && value !== '';
}

// better-sqlite3 refuses to run a string containing more than one statement
// through prepare().all() - that guard rail doesn't exist in the original
// Spring/H2 stack, where the concatenated string is handed to the driver as
// one blob and every ';'-separated statement gets executed. To reproduce
// that behavior (and let the classic "...'; DELETE FROM Book --" payload
// actually delete rows) we split the final string on ';' ourselves and run
// each statement in order - the injection itself is still the naive string
// concatenation below, this only restores the "engine" side effect.
function runStacked(query) {
  const statements = query
    .split(';')
    .map((s) => s.trim())
    .filter(Boolean);

  let rows = [];
  statements.forEach((stmt, i) => {
    if (/^select/i.test(stmt)) {
      const result = db.prepare(stmt).all();
      if (i === 0) rows = result;
    } else {
      db.prepare(stmt).run();
    }
  });
  return rows;
}

// Vulnerable: builds the SQL query by string concatenation, just like the
// original Spring `BookService.findAll()`. The `lang` query param (and to a
// lesser extent `from`/`to`) is injected straight into the SQL string with no
// parameterization/escaping.
//
// Example exploits:
//   /api/books/?lang=es' or '1'='1
//   /api/books/?from=1900&to=2000&lang=es' or '1' = '1'; DELETE FROM book  --
function findAll(from, to, lang) {
  let query = 'SELECT * FROM book';
  if ((from && to) || isNotEmptyField(lang)) query += ' WHERE';
  if (from && to) query += ` publication_year BETWEEN ${from} AND ${to}`;
  if (from && to && isNotEmptyField(lang)) query += ' AND';
  if (isNotEmptyField(lang)) query += ` lang='${lang}'`;
  return runStacked(query);
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
  console.log(`Try: http://localhost:${PORT}/api/books/?from=1900&to=2000&lang=es' or '1' = '1'; DELETE FROM book  --`);
});
