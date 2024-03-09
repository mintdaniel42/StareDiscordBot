import discord
import requests
from config import ENTRIES_PER_PAGE, HIGHSCORE_ICON, COLOR


class Entry(discord.Embed):
    def __init__(self, uuid, rating="---", points=0, joined="---", secondary=False, banned=False, cheating=False):
        self.userdata = requests.request("GET", "https://playerdb.co/api/player/minecraft/" + uuid).json()
        if self.userdata['success']:
            super().__init__(title="Hide 'n' Seek Spielerdatenbank",
                             description=f"Spielername: {self.userdata['data']['player']['username']}",
                             color=COLOR)
            self.set_thumbnail(url=f"https://mc-heads.net/avatar/{uuid}")
            self.add_field(name="Tierlistrating", value=rating, inline=True)
            self.add_field(name="Geschätzte Punkte", value=str(points), inline=True)
            self.add_field(name="im Modus seit",
                           value=joined,
                           inline=True)
            self.add_field(name="Zweitaccount", value="✅" if secondary else "❌", inline=True)
            self.add_field(name="Gebannt", value="✅" if banned else "❌", inline=True)
            self.add_field(name="Cheater", value="✅" if cheating else "❌", inline=True)
        else:
            super().__init__(title="Hide 'n' Seek Spielerdatenbank",
                             description=f"{uuid} konnte nicht gefunden werden",
                             color=COLOR)


class List(discord.Embed):
    def __init__(self, page, db):
        self.entries = db.get_entries(page)
        super().__init__(title="Hide 'n' Seek Spielerdatenbank",
                         description=f"Spieler durchsuchen (Seite {str(page + 1)} / {db.get_pages()})",
                         color=COLOR)
        for entry in range(len(self.entries)):
            username = requests.request("GET", "https://playerdb.co/api/player/minecraft/" + self.entries[entry]).json()['data']['player']['username']
            self.add_field(name=f"#{ENTRIES_PER_PAGE * page + entry + 1}",
                           value=username, inline=False)
