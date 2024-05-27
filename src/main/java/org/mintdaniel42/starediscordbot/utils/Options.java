package org.mintdaniel42.starediscordbot.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

/**
 * This class is used for providing options that are not known at compile time and
 * set later on through environment variables
 */
@UtilityClass
public class Options {
    /**
     * The discord token used for the discord bot
     */
    @Getter private final String token = System.getenv("DISCORD_TOKEN");
    /**
     * The JDBC Url used for creating and connecting to the database
     */
    @Getter private final String jdbcUrl = "jdbc:sqlite:.data/data.db";

    /**
     * The ID of the guild to make sure the Bot is used on only one guild
     */
    @Getter private final long guildId = Long.parseLong(System.getenv("GUILD_ID"));
    /**
     * The ID of the role which allows to edit entries of groups & users
     */
    @Getter private final long editRoleId = Long.parseLong(System.getenv("EDIT_ROLE_ID"));
    /**
     * The ID of the role which allows to create & edit entries of groups & users
     */
    @Getter private final long createRoleId = Long.parseLong(System.getenv("CREATE_ROLE_ID"));
    /**
     * The ID of the channel where edit requests are sent to in case someone without
     * sufficient permission tries to do so
     */
    @Getter private final long requestChannelId = Long.parseLong(System.getenv("REQUEST_CHANNEL_ID"));

    /**
     * The color all embeds should have
     */
    @Getter private final int colorNormal = Integer.parseInt(System.getenv("COLOR_NORMAL"), 16);
    /**
     * The color the request embeds have
     */
    @Getter private final int colorRequest = Integer.parseInt(System.getenv("COLOR_REQUEST"), 16);

    /**
     * The Bot maintenance flag
     */
    @Getter @Setter private boolean inMaintenance = false;
}
