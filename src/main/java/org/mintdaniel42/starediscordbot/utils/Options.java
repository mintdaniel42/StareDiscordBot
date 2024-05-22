package org.mintdaniel42.starediscordbot.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Options {
    @Getter private final String token = System.getenv("DISCORD_TOKEN");
    @Getter private final String jdbcUrl = "jdbc:sqlite:.data/data.db";

    @Getter private final long guildId = Long.parseLong(System.getenv("GUILD_ID"));
    @Getter private final long editRoleId = Long.parseLong(System.getenv("EDIT_ROLE_ID"));
    @Getter private final long createRoleId = Long.parseLong(System.getenv("CREATE_ROLE_ID"));
    @Getter private final long requestChannelId = Long.parseLong(System.getenv("REQUEST_CHANNEL_ID"));

    @Getter private final int colorNormal = Integer.parseInt(System.getenv("COLOR_NORMAL"), 16);
    @Getter private final int colorRequest = Integer.parseInt(System.getenv("COLOR_REQUEST"), 16);

    @Getter @Setter private boolean inMaintenance = false;
}
