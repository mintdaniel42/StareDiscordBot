package org.mintdaniel42.starediscordbot.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

import java.awt.*;
import java.util.Locale;
import java.util.Random;

@UtilityClass
public class Options {
    @Getter private final String token = System.getenv("DISCORD_TOKEN");
    @Getter private final String jdbcUrl = "jdbc:sqlite:.data/data.db";

    @Getter private final long guildId = Long.parseLong(System.getenv("GUILD_ID"));
    @Getter private final long editRoleId = Long.parseLong(System.getenv("EDIT_ROLE_ID"));
    @Getter private final long createRoleId = Long.parseLong(System.getenv("CREATE_ROLE_ID"));

    @Getter private final Locale locale = Locale.GERMANY;

    private final int colorNormal = Integer.parseInt(System.getenv("COLOR_NORMAL"), 16);
    private final int colorRequest = Integer.parseInt(System.getenv("COLOR_REQUEST"), 16);

    @Getter private final byte entriesPerPage = Byte.parseByte(System.getenv("ENTRIES_PER_PAGE"));
    private final byte maxRandomness = 0x30;

    @Setter @Getter private boolean inMaintenance = false;

    public static Color getColorNormal() {
        return new Color(colorNormal + new Random().nextInt(maxRandomness * 2) - maxRandomness);
    }

    public static Color getColorRequest() {
        return new Color(colorRequest + new Random().nextInt(maxRandomness * 2) - maxRandomness);
    }
}
