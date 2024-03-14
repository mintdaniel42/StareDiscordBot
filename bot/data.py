import sqlite3
from math import ceil
from config import ENTRIES_PER_PAGE
from time import time


class Database:
    def __init__(self) -> None:
        self.connection = sqlite3.connect(".data/data.db")
        self.cursor = self.connection.cursor()
        self.cursor.execute("CREATE TABLE IF NOT EXISTS entries (uuid TEXT primary key, rating TEXT, "
                            "points INTEGER, joined TEXT, secondary INTEGER, banned INTEGER, cheating INTEGER)")
        self.cursor.execute("CREATE TABLE IF NOT EXISTS requests (timestamp INTEGER, uuid TEXT, key TEXT, value TEXT)")

    def get_entry(self, uuid: str) -> dict:
        self.cursor.execute("SELECT * FROM entries WHERE uuid = ?", (uuid,))
        result = self.cursor.fetchone()
        if result is None:
            return {}
        return {"rating": result[1], "points": result[2], "joined": result[3],
                "secondary": result[4], "banned": result[5], "cheating": result[6]}

    def edit_entry(self, uuid: str, key: str, value) -> None:
        self.cursor.execute(f"UPDATE entries SET {key} = ? WHERE uuid = ?", (value, uuid))
        self._save()

    def add_entry(self, uuid: str, rating: str, points: int, joined: str, secondary: bool, banned: bool,
                  cheating: bool) -> None:
        self.cursor.execute("INSERT INTO entries VALUES (?, ?, ?, ?, ?, ?, ?)", (uuid, rating, points, joined,
                                                                                 1 if secondary else 0,
                                                                                 1 if banned else 0,
                                                                                 1 if cheating else 0))
        self._save()

    def has_entry(self, uuid: str) -> bool:
        self.cursor.execute("SELECT uuid FROM entries WHERE uuid = ?", (uuid,))
        return self.cursor.fetchone() is not None

    def get_entries(self, page) -> list:
        self.cursor.execute("SELECT uuid, points FROM entries ORDER BY points DESC LIMIT ?, ?",
                            (page * ENTRIES_PER_PAGE, ENTRIES_PER_PAGE))
        result = self.cursor.fetchall()
        entries = list()
        for entry in result:
            entries.append(entry[0])
        return entries

    def get_pages(self) -> int:
        self.cursor.execute("SELECT COUNT(uuid) FROM entries")
        return ceil(self.cursor.fetchone()[0] / ENTRIES_PER_PAGE)

    def add_request(self, timestamp: int, uuid: str, key: str, value: str | int):
        self._purge_requests()
        self.cursor.execute("DELETE FROM requests WHERE key = ? AND uuid = ?",
                            (key, uuid))
        self.cursor.execute("INSERT INTO requests VALUES (?, ?, ?, ?)", (timestamp, uuid, key, value))
        self._save()

    def approve_request(self, timestamp: int) -> str:
        self.cursor.execute("SELECT * FROM requests WHERE timestamp = ?", (timestamp,))
        result = self.cursor.fetchone()
        uuid = None
        if result:
            self.cursor.execute(f"UPDATE entries SET {result[2]} = ? WHERE uuid = ?", (result[3], result[1]))
            self.cursor.execute("SELECT uuid, timestamp FROM requests WHERE timestamp = ?", (timestamp,))
            uuid = self.cursor.fetchone()[0]
        self.cursor.execute("DELETE FROM requests WHERE timestamp = ?", (timestamp,))
        self._save()
        return uuid

    def _purge_requests(self):
        self.cursor.execute("DELETE FROM requests WHERE timestamp > ?", (round(time() * 1000) + 124800000,))

    def _save(self):
        if self.connection.total_changes % 5 == 0:
            self.connection.commit()

    def close(self) -> None:
        self.cursor.close()
        self.connection.close()
