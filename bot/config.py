import os

GUILD_ID = int(os.getenv('GUILD_ID'))
DISCORD_TOKEN = os.getenv('DISCORD_TOKEN')
EDIT_ROLE_ID = os.getenv('EDIT_ROLE_ID')
CREATE_ROLE_ID = os.getenv('CREATE_ROLE_ID')
VIEW_ROLE_ID = os.getenv('VIEW_ROLE_ID')
EPHEMERAL = True if os.getenv('EPHEMERAL') == 1 else 0