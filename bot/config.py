import os

GUILD_ID = int(os.getenv('GUILD_ID'))
DISCORD_TOKEN = os.getenv('DISCORD_TOKEN')
EDIT_ROLE_ID = os.getenv('EDIT_ROLE_ID')
CREATE_ROLE_ID = os.getenv('CREATE_ROLE_ID')
VIEW_ROLE_ID = os.getenv('VIEW_ROLE_ID')
EPHEMERAL = True if os.getenv('EPHEMERAL') == 1 else 0
ENTRIES_PER_PAGE = int(os.getenv('ENTRIES_PER_PAGE'))
HIGHSCORE_ICON = os.getenv('HIGHSCORE_ICON')
COLOR = int(os.getenv('COLOR'), 16)