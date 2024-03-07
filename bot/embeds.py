import datetime

import discord
import requests


class Entry(discord.Embed):
    def __init__(self, uuid, rating="---", points=0, joined="---", secondary=False, banned=False, cheating=False):
        self.userdata = requests.request("GET", "https://playerdb.co/api/player/minecraft/" + uuid).json()
        if (self.userdata['success']):
            super().__init__(title="Hide 'n' Seek Spielerdatenbank",
                             description=f"Spielername: {self.userdata['data']['player']['username']}",
                             color=0x00ff00)
            self.set_thumbnail(url=f"https://mc-heads.net/avatar/{uuid}")
            self.add_field(name="Tierlistrating", value=rating, inline=True)
            self.add_field(name="Geschätzte Punkte", value=points, inline=True)
            self.add_field(name="im Modus seit",
                           value=joined,
                           inline=True)
            self.add_field(name="Zweitaccount", value="✅" if secondary else "❌", inline=True)
            self.add_field(name="Gebannt", value="✅" if banned else "❌", inline=True)
            self.add_field(name="Cheater", value="✅" if cheating else "❌", inline=True)
        else:
            super().__init__(title="Hide 'n' Seek Spielerdatenbank",
                             description=f"{uuid} konnte nicht gefunden werden",
                             color=0xff0000)
