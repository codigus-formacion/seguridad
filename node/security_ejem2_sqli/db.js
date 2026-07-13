const Database = require('better-sqlite3');

const db = new Database(':memory:');

db.exec(`
  CREATE TABLE book (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT,
    description TEXT,
    publication_year INTEGER,
    lang TEXT
  );
`);

const insert = db.prepare(
  'INSERT INTO book (title, description, publication_year, lang) VALUES (?, ?, ?, ?)'
);

insert.run('The Lord of the Rings', 'An epic fantasy novel by J.R.R. Tolkien.', 1954, 'en');
insert.run('Don Quixote', 'A Spanish novel by Miguel de Cervantes.', 1605, 'es');
insert.run('The Great Gatsby', 'A novel by F. Scott Fitzgerald.', 1925, 'en');
insert.run('One Hundred Years of Solitude', 'A novel by Gabriel García Márquez.', 1967, 'es');

module.exports = db;
