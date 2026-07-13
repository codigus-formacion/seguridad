import sqlite3

conn = sqlite3.connect(":memory:", check_same_thread=False)
conn.row_factory = sqlite3.Row

conn.execute(
    """
    CREATE TABLE book (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        title TEXT,
        description TEXT,
        publication_year INTEGER,
        lang TEXT
    )
    """
)

books = [
    ("The Lord of the Rings", "An epic fantasy novel by J.R.R. Tolkien.", 1954, "en"),
    ("Don Quixote", "A Spanish novel by Miguel de Cervantes.", 1605, "es"),
    ("The Great Gatsby", "A novel by F. Scott Fitzgerald.", 1925, "en"),
    ("One Hundred Years of Solitude", "A novel by Gabriel García Márquez.", 1967, "es"),
]
conn.executemany(
    "INSERT INTO book (title, description, publication_year, lang) VALUES (?, ?, ?, ?)",
    books,
)
conn.commit()
