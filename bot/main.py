import discord
from discord.ext import commands

from config import GUILD_ID, DISCORD_TOKEN, VIEW_ROLE_ID, EDIT_ROLE_ID, CREATE_ROLE_ID, EPHEMERAL
from embeds import Entry, List
from data import Database

import requests

bot = commands.Bot()
db = Database()


@bot.slash_command(name="showuser", description="Eintrag anzeigen", guild_ids=[GUILD_ID])
async def show_user(ctx, username: str):
    if discord.utils.get(ctx.guild.roles, id=int(VIEW_ROLE_ID)) not in ctx.author.roles:
        await ctx.respond("Du darfst diesen Befehl nicht benutzen!", ephemeral=EPHEMERAL)
        return
    response = requests.request("GET", "https://playerdb.co/api/player/minecraft/" + username).json()
    if not response['success']:
        await ctx.respond(f"Der Nutzer {username} existiert nicht!", ephemeral=EPHEMERAL)
        return
    uuid = response['data']['player']['id']
    await ctx.respond(embed=Entry(
        uuid,
        **db.get_entry(uuid)
    ), ephemeral=EPHEMERAL)


@bot.slash_command(name="listusers", description="Einträge auflisten", guild_ids=[GUILD_ID])
async def list_users(ctx, page: int = 1):
    if discord.utils.get(ctx.guild.roles, id=int(VIEW_ROLE_ID)) not in ctx.author.roles:
        await ctx.respond("Du darfst diesen Befehl nicht benutzen!", ephemeral=EPHEMERAL)
        return
    if 0 >= page or page > db.get_pages():
        await ctx.respond(f"Die Seite {page} gibt es nicht!", ephemeral=EPHEMERAL)
        return
    await ctx.respond(embed=List(page - 1, db))


@bot.slash_command(name="edituser", description="Eintrag bearbeiten", guild_ids=[GUILD_ID])
async def edit_user(ctx, username: str, key: str, value: str):
    if discord.utils.get(ctx.guild.roles, id=int(EDIT_ROLE_ID)) not in ctx.author.roles:
        await ctx.respond("Du darfst diesen Befehl nicht benutzen!", ephemeral=EPHEMERAL)
        return
    response = requests.request("GET", "https://playerdb.co/api/player/minecraft/" + username).json()
    if not response['success']:
        await ctx.respond(f"Der Nutzer {username} existiert nicht!", ephemeral=EPHEMERAL)
        return
    uuid = response['data']['player']['username']
    if key in ("secondary", "banned", "cheating"):
        db.edit_entry(uuid, key, 1 if value.lower() in ("1", "true", "yes", "ja") else 0)
    elif key == "points":
        db.edit_entry(uuid, key, int(value))
    elif key in ("rating", "joined"):
        db.edit_entry(uuid, key, value)
    else:
        await ctx.respond("Dieses Feld existiert nicht", ephemeral=EPHEMERAL)
        return
    await ctx.respond(f"Die Änderungen am Feld {key} wurden gespeichert!", ephemeral=EPHEMERAL)


@bot.slash_command(name="adduser", description="Eintrag hinzufügen", guild_ids=[GUILD_ID])
async def add_user(ctx, username: str, rating: str, points: int,
                   joined: str, secondary: bool, banned: bool, cheating: bool):
    if discord.utils.get(ctx.guild.roles, id=int(CREATE_ROLE_ID)) not in ctx.author.roles:
        await ctx.respond("Du darfst diesen Befehl nicht benutzen!", ephemeral=EPHEMERAL)
        return
    response = requests.request("GET", "https://playerdb.co/api/player/minecraft/" + username).json()
    if not response['success']:
        await ctx.respond(f"Der Nutzer {username} existiert nicht!", ephemeral=EPHEMERAL)
        return
    uuid = response['data']['player']['id']
    if db.has_entry(uuid):
        await ctx.respond(f"Der Eintrag für den Nutzer {username} existiert bereits!", ephemeral=EPHEMERAL)
        return
    db.add_entry(uuid, rating, points, joined, secondary, banned, cheating)
    await ctx.respond(f"Der Eintrag für den Nutzer {username} wurde angelegt!", ephemeral=EPHEMERAL)

try:
    bot.run(DISCORD_TOKEN)
finally:
    db.close()
