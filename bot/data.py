import sqlite3


class Database:
    def __init__(self):
        self.connection = sqlite3.connect("data.db")
        self.cursor = self.connection.cursor()
        self.cursor.execute("CREATE TABLE IF NOT EXISTS entries (uuid TEXT, rating TEXT, "
                            "points INTEGER, joined TEXT, secondary INTEGER, banned INTEGER, cheating INTEGER)")

    def get_entry(self, uuid: str):
        self.cursor.execute("SELECT * FROM entries WHERE uuid = ?", (uuid,))
        result = self.cursor.fetchone()
        if result is None:
            return {}
        return {"rating": result[1], "points": result[2], "joined": result[3],
                "secondary": result[4], "banned": result[5], "cheating": result[6]}

    def edit_entry(self, uuid: str, key: str, value):
        self.cursor.execute(f"UPDATE entries SET {key} = ? WHERE uuid = ?", (value, uuid))
        self.connection.commit()

    def add_entry(self, uuid: str, rating: str, points: int, joined: str, secondary: bool, banned: bool,
                  cheating: bool):
        self.cursor.execute("INSERT INTO entries VALUES (?, ?, ?, ?, ?, ?, ?)", (uuid, rating, points, joined,
                                                                                 1 if secondary else 0,
                                                                                 1 if banned else 0,
                                                                                 1 if cheating else 0))
        self.connection.commit()

    def has_entry(self, uuid: str):
        self.cursor.execute("SELECT uuid FROM entries WHERE uuid = ?", (uuid,))
        return self.cursor.fetchone() is not None

    def close(self):
        self.cursor.close()
        self.connection.close()
