import os
import time

import discord
from discord import app_commands

from config import GUILD_ID, DISCORD_TOKEN
from embeds import Entry

intents = discord.Intents.default()
client = discord.Client(intents=intents)
tree = app_commands.CommandTree(client)


@tree.command(name="edituser", description="Eintrag bearbeiten", guild=discord.Object(id=GUILD_ID))
async def edit_user(interaction):
    await interaction.response.send_message("Hello!")


@tree.command(name="showuser", description="Eintrag ansehen", guild=discord.Object(id=GUILD_ID))
async def edit_user(interaction):
    await interaction.response.send_message("Hello!")


@client.event
async def on_message(message):
    if message.author == client.user:
        return

    await message.channel.send(embed=Entry(
        "MintDaniel42",
        **{
            "rating": "#1 | S-Tier",
            "points": 42_000_000,
            "joined": time.time(),
            "secondary": False,
            "banned": True,
            "cheating": False,
        }
    ))

client.run(DISCORD_TOKEN)
