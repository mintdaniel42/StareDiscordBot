import os
import time

import discord
from discord.ext import commands

from config import GUILD_ID, DISCORD_TOKEN
from embeds import Entry

bot = commands.Bot()


@bot.slash_command(name="showuser", description="Eintrag anzeigen", guild_ids=[GUILD_ID])
async def edit_user(ctx, username: str):
    await ctx.respond(embed=Entry(
        username,
        **{
            "rating": "#1 | S-Tier",
            "points": 42_000_000,
            "joined": time.time(),
            "secondary": False,
            "banned": True,
            "cheating": False,
        }
    ))

bot.run(DISCORD_TOKEN)
