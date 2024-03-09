import discord

from embeds import List


class ListButtons(discord.ui.View):
    def __init__(self, page, db):
        super().__init__()
        self.page = page
        self.db = db

    @discord.ui.button(label="Nächste Seite", style=discord.ButtonStyle.primary)
    async def button_callback(self, button, interaction):
        await interaction.respond(embed=List(self.page, self.db), view=ListButtons(self.page, self.db))

    @discord.ui.button(label="Vorherige Seite", style=discord.ButtonStyle.primary)
    async def button_callback(self, button, interaction):
        await interaction.response.send_message(ListButtons(self.page - 1))
