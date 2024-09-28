package org.mintdaniel42.starediscordbot.utils;

import io.avaje.config.Config;
import lombok.Getter;
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
     * The color all embeds should have
     */
    @Getter private final int colorNormal = Integer.parseInt(Config.get("color.normal"), 16);

    /**
     * The color the request embeds have
     */
    @Getter private final int colorRequest = Integer.parseInt(Config.get("color.request"), 16);
}
