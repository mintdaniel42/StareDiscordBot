package org.mintdaniel42.starediscordbot.utils;

import io.avaje.config.Config;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.mintdaniel42.starediscordbot.build.BuildConfig;

/**
 * This class is used for providing options that are not known at compile time and
 * set later on through environment variables
 */
@UtilityClass
@Deprecated(forRemoval = BuildConfig.production)
public class Options {
    /**
     * The ID of the role which allows to edit entries of groups & users
     */
    @Getter private final long p2Id = Config.getLong("p2.role.id");

    /**
     * Additional role (currently) only used for spot token-bucket mechanism
     */
    @Getter private final long p3Id = Config.getLong("p3.role.id");

    /**
     * The ID of the role which allows to create & edit entries of groups & users
     */
    @Getter private final long p4Id = Config.getLong("p4.role.id");

    /**
     * The color all embeds should have
     */
    @Getter private final int colorNormal = Integer.parseInt(Config.get("color.normal"), 16);

    /**
     * The color the request embeds have
     */
    @Getter private final int colorRequest = Integer.parseInt(Config.get("color.request"), 16);

    /**
     * The Bot maintenance flag
     */
    @Getter @Setter private boolean inMaintenance = false;
}
