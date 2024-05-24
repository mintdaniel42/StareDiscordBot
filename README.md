# Welcome to the Stare / Prinzessinnen Discord Bot project!

Before you start, ensure you have the following:

- Docker: Ensure Docker is set up and running to manage dependencies and deployment smoothly.

## Environment Variables

You need to set the following environment variables for the bot to function correctly. These IDs should be valid Discord role, guild, and channel IDs, and the color values should be hex colors without the #:

```
DISCORD_TOKEN: Your Discord bot token.
GUILD_ID: The ID of your Discord guild (server).
EDIT_ROLE_ID: The ID of the role that can edit.
CREATE_ROLE_ID: The ID of the role that can create.
VIEW_ROLE_ID: The ID of the role that can view.
REQUEST_CHANNEL_ID: The ID of the request channel.
COLOR_NORMAL: Hex color code for normal messages.
COLOR_REQUEST: Hex color code for request messages.
```

Example .env file

```
DISCORD_TOKEN=your_discord_token_here
GUILD_ID=your_guild_id_here
EDIT_ROLE_ID=your_edit_role_id_here
CREATE_ROLE_ID=your_create_role_id_here
VIEW_ROLE_ID=your_view_role_id_here
REQUEST_CHANNEL_ID=your_request_channel_id_here
COLOR_NORMAL=ffffff
COLOR_REQUEST=ff0000
```

## Updating the Bot

To update the bot to the latest version, use this command in your command line or terminal. This will fetch the latest changes and restart the bot with the new updates.

```
curl https://raw.githubusercontent.com/mintdaniel42/StareDiscordBot/master/updater.sh | bash
```

## Run the bot

(this should not be necessary if you used the previous command)

```
docker-compose up -d
```

And you're all set! The bot should now be running and active in your Discord server.

Feel free to fork this project and make your own improvements. Pull requests are always welcome!

See the [LICENSE file](LICENSE.md) for details.