import random
import time

import discord
from discord.ext import commands

from config import GUILD_ID, DISCORD_TOKEN, VIEW_ROLE_ID, EDIT_ROLE_ID, CREATE_ROLE_ID, EPHEMERAL, REQUEST_CHANNEL_ID
from embeds import Entry, List, Error, ApprovalRequired
from data import Database
from util import validate_string_format, convert_string_to_int
from modals import MyModal
from views import ListButtons

import requests

bot = commands.Bot()
db = Database()


async def handle_error(ctx, command: str, exception: Exception) -> None:
    await ctx.respond(embed=Error(command, exception), ephemeral=True)


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
    if random.random() >= .2:
        await bot.change_presence(activity=discord.Activity(name=username, type=discord.ActivityType.watching))
    elif random.random() >= 0.9:
        await bot.change_presence(activity=discord.Activity(name="Minecraft", type=discord.ActivityType.playing))
    else:
        await bot.change_presence(activity=None)


@bot.slash_command(name="listusers", description="Einträge auflisten", guild_ids=[GUILD_ID])
async def list_users(ctx, page: int = 1):
    print(f"/listusers triggered by {ctx.author}")
    if discord.utils.get(ctx.guild.roles, id=int(VIEW_ROLE_ID)) not in ctx.author.roles:
        await ctx.respond("Du darfst diesen Befehl nicht benutzen!", ephemeral=EPHEMERAL)
        return
    if 0 >= page or page > db.get_pages():
        await ctx.respond(f"Die Seite {page} gibt es nicht!", ephemeral=EPHEMERAL)
        return
    await ctx.respond(embed=List(page - 1, db), view=ListButtons(page - 1, db))
    print(f"/listusers finished")


@bot.slash_command(name="edituser", description="Eintrag bearbeiten", guild_ids=[GUILD_ID])
async def edit_user(ctx, username: str, key: str, value: str):
    try:
        if discord.utils.get(ctx.guild.roles, id=int(VIEW_ROLE_ID)) not in ctx.author.roles:
            await ctx.respond("Du darfst diesen Befehl nicht benutzen!", ephemeral=EPHEMERAL)
            return
        response = requests.request("GET", "https://playerdb.co/api/player/minecraft/" + username).json()
        if not response['success']:
            await ctx.respond(f"Der Nutzer {username} existiert nicht!", ephemeral=EPHEMERAL)
            return
        uuid = response['data']['player']['id']
        if key in ("secondary", "banned", "cheating"):
            if discord.utils.get(ctx.guild.roles, id=int(EDIT_ROLE_ID)) not in ctx.author.roles:
                timestamp: int = round(time.time() * 1000)
                db.add_request(timestamp, uuid, key,
                               1 if value.lower() in ("1", "true", "yes", "ja") else 0)
                await ctx.respond(f"Die Änderungen am Feld `{key}` für den Eintrag `\"{username}\"` wurden gespeichert "
                                  "und werden als Nächstes von einem Admin oder"
                                  "Moderator geprüft und (hoffentlich) freigegeben.")
                await bot.get_channel(REQUEST_CHANNEL_ID).send(embed=ApprovalRequired(timestamp, uuid, key, value,
                                                                                      db.get_entry(uuid)[key]))
                return
            db.edit_entry(uuid, key, 1 if value.lower() in ("1", "true", "yes", "ja") else 0)
        elif key == "points":
            if not validate_string_format(value):
                await ctx.respond("Das ist keine gültige Zahl!", ephemeral=EPHEMERAL)
                return
            if discord.utils.get(ctx.guild.roles, id=int(EDIT_ROLE_ID)) not in ctx.author.roles:
                timestamp: int = round(time.time() * 1000)
                db.add_request(timestamp, uuid, key, convert_string_to_int(value))
                await ctx.respond(f"Die Änderungen am Feld `{key}` für den Eintrag `\"{username}\"` wurden gespeichert "
                                  "und werden als Nächstes von einem Admin oder "
                                  "Moderator geprüft und (hoffentlich) freigegeben.")
                await bot.get_channel(REQUEST_CHANNEL_ID).send(embed=ApprovalRequired(timestamp, uuid, key, value,
                                                                                      db.get_entry(uuid)[key]))
                return
            db.edit_entry(uuid, key, convert_string_to_int(value))
        elif key in ("rating", "joined"):
            if discord.utils.get(ctx.guild.roles, id=int(EDIT_ROLE_ID)) not in ctx.author.roles:
                timestamp: int = round(time.time() * 1000)
                db.add_request(timestamp, uuid, key, value)
                await ctx.respond(f"Die Änderungen am Feld `{key}` für den Eintrag `\"{username}\"` wurden gespeichert "
                                  "und werden als Nächstes von einem Admin oder"
                                  "Moderator geprüft und (hoffentlich) freigegeben.")
                await bot.get_channel(REQUEST_CHANNEL_ID).send(embed=ApprovalRequired(timestamp, uuid, key, value,
                                                                                      db.get_entry(uuid)[key]))
                return
            db.edit_entry(uuid, key, value)
        else:
            await ctx.respond("Dieses Feld existiert nicht!", ephemeral=EPHEMERAL)
            return
        await ctx.respond(f"Die Änderungen am Feld `{key}` für den Eintrag `\"{username}\"` wurden gespeichert!",
                          embed=Entry(uuid, **db.get_entry(uuid)), ephemeral=EPHEMERAL)
    except Exception as exception:
        await handle_error(ctx, f"/edituser key {key} value {value}", exception)


@bot.slash_command(name="adduser", description="Eintrag hinzufügen", guild_ids=[GUILD_ID])
async def add_user(ctx, username: str, rating: str, points: str,
                   joined: str, secondary: bool = False, banned: bool = False, cheating: bool = False):
    try:
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
        if not validate_string_format(points):
            await ctx.respond("Der Wert vom Feld `points` ist keine gültige Zahl!", ephemeral=EPHEMERAL)
            return
        db.add_entry(uuid, rating, convert_string_to_int(points), joined, secondary, banned, cheating)
        await ctx.respond(f"Der Eintrag für den Nutzer {username} wurde angelegt!",
                          embed=Entry(uuid, **db.get_entry(uuid)),
                          ephemeral=EPHEMERAL)
    except Exception as exception:
        await handle_error(ctx, f"/adduser username {username} rating {rating} points {points}" +
                           f" joined {joined} secondary {secondary} banned {banned} cheating {cheating}",
                           exception)


@bot.slash_command(name="approve", description="Änderungen freigeben", guild_ids=[GUILD_ID])
async def approve(ctx, timestamp: int):
    try:
        if discord.utils.get(ctx.guild.roles, id=int(EDIT_ROLE_ID)) not in ctx.author.roles:
            await ctx.respond("Du darfst diesen Befehl nicht benutzen!", ephemeral=EPHEMERAL)
            return
        if not db.has_request(timestamp):
            await ctx.respond("Die Änderung wurde bereits freigegeben oder existiert nicht mehr!")
            return
        uuid = db.approve_request(timestamp)
        await ctx.respond("Die Änderung wurde erfolgreich freigegeben!", embed=Entry(uuid, **db.get_entry(uuid)))
    except Exception as exception:
        await handle_error(ctx, f"/approve timestamp {timestamp}", exception)


@bot.slash_command()
async def modal_slash(ctx: discord.ApplicationContext):
    """Shows an example of a modal dialog being invoked from a slash command."""
    modal = MyModal(title="Modal via Slash Command")
    await ctx.send_modal(modal)


try:
    bot.run(DISCORD_TOKEN)
finally:
    db.close()
