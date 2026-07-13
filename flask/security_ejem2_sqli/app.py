import os

from flask import Flask, request, jsonify

from db import conn

app = Flask(__name__)


def is_not_empty_field(value):
    return value is not None and value != ""


# Python's sqlite3 refuses to run more than one statement through a single
# execute() call - that guard rail doesn't exist in the original Spring/H2
# stack, where the concatenated string is handed to the driver as one blob
# and every ';'-separated statement gets executed. To reproduce that
# behavior (and let the classic "...'; DELETE FROM book --" payload actually
# delete rows) we split the final string on ';' ourselves and run each
# statement in order - the injection itself is still the naive string
# concatenation below, this only restores the "engine" side effect.
def run_stacked(query):
    statements = [s.strip() for s in query.split(";") if s.strip()]
    rows = []
    for i, stmt in enumerate(statements):
        cur = conn.execute(stmt)
        if i == 0 and stmt.lower().startswith("select"):
            rows = [dict(row) for row in cur.fetchall()]
    conn.commit()
    return rows


# Vulnerable: builds the SQL query by string concatenation, just like the
# original Spring `BookService.findAll()`. The `lang` query param (and to a
# lesser extent `from`/`to`) is injected straight into the SQL string with no
# parameterization/escaping.
#
# Example exploits:
#   /api/books/?lang=es' or '1'='1
#   /api/books/?from=1900&to=2000&lang=es' or '1' = '1'; DELETE FROM book  --
def find_all(from_year, to_year, lang):
    query = "SELECT * FROM book"
    if (from_year and to_year) or is_not_empty_field(lang):
        query += " WHERE"
    if from_year and to_year:
        query += f" publication_year BETWEEN {from_year} AND {to_year}"
    if from_year and to_year and is_not_empty_field(lang):
        query += " AND"
    if is_not_empty_field(lang):
        query += f" lang='{lang}'"
    return run_stacked(query)


@app.route("/api/books/")
def list_books():
    from_year = request.args.get("from")
    to_year = request.args.get("to")
    lang = request.args.get("lang")
    try:
        return jsonify(find_all(from_year, to_year, lang))
    except Exception as err:
        return jsonify({"error": str(err)}), 500


@app.route("/api/books/<int:book_id>")
def get_book(book_id):
    row = conn.execute("SELECT * FROM book WHERE id = ?", (book_id,)).fetchone()
    if not row:
        return jsonify({"error": "Book not found"}), 404
    return jsonify(dict(row))


if __name__ == "__main__":
    port = int(os.environ.get("PORT", 8080))
    print(f"Try: http://localhost:{port}/api/books/?lang=es' or '1'='1")
    app.run(port=port, debug=True)
