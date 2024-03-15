import traceback

import discord
import requests
from config import ENTRIES_PER_PAGE, COLOR_NORMAl, COLOR_REQUEST
from util import get_ranking_icon


class Entry(discord.Embed):
    def __init__(self, username: str, uuid: str, rating: str = "---", points: int = 0,
                 joined: str = "---", secondary: bool = False, banned: bool = False, cheating: bool = False):
        super().__init__(title="Hide 'n' Seek Spielerdatenbank",
                         description=f"Spielername: {username}",
                         color=COLOR_NORMAl)
        self.set_thumbnail(url=f"https://minotar.net/armor/bust/{uuid.__str__()}")
        self.add_field(name="Tierlistrating", value=rating, inline=True)
        self.add_field(name="Geschätzte Punkte", value="{:,}".format(points).replace(',', '.'), inline=True)
        self.add_field(name="im Modus seit",
                       value=joined,
                       inline=True)
        self.add_field(name="Zweitaccount", value="✅" if secondary else "❌", inline=True)
        self.add_field(name="Gebannt", value="✅" if banned else "❌", inline=True)
        self.add_field(name="Cheater", value="✅" if cheating else "❌", inline=True)


class AdvancedEntry(discord.Embed):
    def __init__(self, username: str, uuid: str, note: str = "---", top10: str = "Nie", group: str = None):
        super().__init__(title="Hide 'n' Seek Spielerdatenbank",
                         description=f"Spielername: {username}",
                         color=COLOR_NORMAl)
        self.set_thumbnail(url=f"https://minotar.net/armor/bust/{uuid.__str__()}")
        self.add_field(name="Notiz", value=note, inline=False)
        self.add_field(name="Top 10", value=top10, inline=True)
        self.add_field(name="Gruppierung", value=group, inline=True)


class GroupEntry(discord.Embed):
    def __init__(self):
        super().__init__()


class List(discord.Embed):
    def __init__(self, page, db):
        self.entries = db.get_entries(page)
        super().__init__(title="Hide 'n' Seek Spielerdatenbank",
                         description=f"Spieler durchsuchen (Seite {str(page + 1)} / {db.get_pages()})",
                         color=COLOR_NORMAl)
        self.set_thumbnail(url=get_ranking_icon(level=page))
        for entry in range(len(self.entries)):
            username = requests.request("GET", "https://playerdb.co/api/player/minecraft/" + self.entries[entry]).json()['data']['player']['username']
            self.add_field(name=f"#{ENTRIES_PER_PAGE * page + entry + 1}",
                           value=username, inline=False)


class Error(discord.Embed):
    def __init__(self, msg: str, exception: Exception) -> None:
        super().__init__(title="Kritischer Fehler", description="Dein Befehl hat zu einem Fehler geführt "
                                                                "(aber keine Sorge, es ist nichts schlimmes passiert). "
                                                                "Schicke einfach einen Screenshot dieser Nachricht "
                                                                "an einen Entwickler oder Admin!", color=COLOR_NORMAl)
        self.add_field(name="Nachricht", value=msg)
        self.add_field(name="Exception", value="\n".join(traceback.format_exception_only(exception)))


class ApprovalRequired(discord.Embed):
    def __init__(self, timestamp: int, uuid: str, key: str, value_new: str | int, value_old: str | int):
        super().__init__(title="Anfrage zum Ändern",
                         description="Ein Nutzer möchte eine Änderung an der Datenbank vornehmen. Führe zum Freigeben "
                                     "der Änderung den unten genannten Befehl aus!", color=COLOR_REQUEST)
        username = requests.request("GET", "https://playerdb.co/api/player/minecraft/" + uuid).json()['data']['player']['username']
        self.add_field(name="Nutzer", value=username)
        self.add_field(name="Feld", value=key)
        self.add_field(name="Neuer Wert", value=value_new)
        self.add_field(name="Alter Wert", value=value_old)
        self.add_field(name="Befehl zum Freigeben", value=f"/approve {timestamp}")
