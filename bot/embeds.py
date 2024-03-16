import traceback

import discord
import requests
from config import ENTRIES_PER_PAGE
from util import get_ranking_icon
from resources import Strings, Colors


class Entry(discord.Embed):
    def __init__(self, username: str, uuid: str, rating: str = "---", points: int = 0,
                 joined: str = "---", secondary: bool = False, banned: bool = False, cheating: bool = False):
        super().__init__(title=Strings.embeds_common_title.format(username=username),
                         description=Strings.embeds_showuser_subtitle.format(username=username),
                         color=Colors.embed_normal)
        self.set_thumbnail(url=f"https://minotar.net/armor/bust/{uuid.__str__()}")
        self.add_field(name=Strings.embeds_showuser_rating_title, value=rating, inline=True)
        self.add_field(name=Strings.embeds_showuser_points_title, value="{:,}".format(points).replace(',', '.'), inline=True)
        self.add_field(name=Strings.embeds_showuser_joined_title,
                       value=joined,
                       inline=True)
        self.add_field(name=Strings.embeds_showuser_secondary_title, value="✅" if secondary else "❌", inline=True)
        self.add_field(name=Strings.embeds_showuser_banned_title, value="✅" if banned else "❌", inline=True)
        self.add_field(name=Strings.embeds_showuser_cheating_title, value="✅" if cheating else "❌", inline=True)


class AdvancedEntry(discord.Embed):
    def __init__(self, username: str, uuid: str, note: str = "---", top10: str = "Nie", group: str = None):
        super().__init__(title=Strings.embeds_common_title.format(username=username),
                         description=Strings.embeds_showuser_subtitle.format(username=username),
                         color=Colors.embed_normal)
        self.set_thumbnail(url=f"https://minotar.net/armor/bust/{uuid.__str__()}")
        self.add_field(name=Strings.embeds_advanced_note_title, value=note, inline=False)
        self.add_field(name=Strings.embeds_advanced_top10_title, value=top10, inline=True)
        self.add_field(name=Strings.embeds_advanced_group_title, value=group, inline=True)


class GroupEntry(discord.Embed):
    def __init__(self):
        super().__init__()


class List(discord.Embed):
    def __init__(self, page, db):
        self.entries = db.get_entries(page)
        super().__init__(title=Strings.embeds_common_title,
                         description=Strings.embeds_list_subtitle.format(page=page + 1, pages=db.get_pages()),
                         color=Colors.embed_normal)
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
                                                                "an einen Entwickler oder Admin!", color=Colors.embed_normal)
        self.add_field(name="Nachricht", value=msg)
        self.add_field(name="Exception", value="\n".join(traceback.format_exception_only(exception)))


class ApprovalRequired(discord.Embed):
    def __init__(self, timestamp: int, uuid: str, key: str, value_new: str | int, value_old: str | int):
        super().__init__(title=Strings.embeds_request_title,
                         description=Strings.embeds_request_subtitle,
                         color=Colors.embed_request)
        username = requests.request("GET", "https://playerdb.co/api/player/minecraft/" + uuid).json()['data']['player']['username']
        self.add_field(name=Strings.embeds_request_user_title, value=username)
        self.add_field(name=Strings.embeds_request_field_title, value=key)
        self.add_field(name=Strings.embeds_request_new_title, value=value_new)
        self.add_field(name=Strings.embeds_request_old_title, value=value_old)
        self.add_field(name=Strings.embeds_request_command_title, value=f"/approve {timestamp}")
