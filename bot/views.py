import discord

from embeds import List, AdvancedEntry, GroupEntry
from data import Database


class MoreInformationButton(discord.ui.View):
    def __init__(self, username: str, uuid: str):
        super().__init__()
        self.username = username
        self.uuid = uuid

    @discord.ui.button(label="Mehr Informationen", style=discord.ButtonStyle.primary, disabled=True)
    async def more_information_callback(self, button, interaction):
        await interaction.respond(embed=AdvancedEntry(username=self.username, uuid=self.uuid))

    @discord.ui.button(label="Gruppierung anzeigen", style=discord.ButtonStyle.primary, disabled=True)
    async def show_group_callback(self, button, interaction):
        await interaction.respond(embed=GroupEntry())


class ListButtons(discord.ui.View):
    def __init__(self, page: int, db: Database):
        super().__init__()
        self.page = page
        self.db = db

    @discord.ui.button(label="Vorherige Seite", row=0, style=discord.ButtonStyle.primary)
    async def previous_page_callback(self, button, interaction):
        await interaction.respond(embed=List(max(self.page - 1, 0), self.db),
                                  view=ListButtons(max(self.page, 0), self.db))

    @discord.ui.button(label="Nächste Seite", row=0, style=discord.ButtonStyle.primary)
    async def next_page_callback(self, button, interaction):
        await interaction.respond(embed=List(min(self.page + 1, self.db.get_pages()), self.db),
                                  view=ListButtons(min(self.page + 1, self.db.get_pages()), self.db))
