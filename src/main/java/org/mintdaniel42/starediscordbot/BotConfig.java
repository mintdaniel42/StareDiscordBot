package org.mintdaniel42.starediscordbot;

import io.avaje.config.Config;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;

/**
 * This class is used for providing options that are not known at compile time and
 * set later on through config files
 */
@RequiredArgsConstructor
@Singleton
@Value
public class BotConfig {
	/**
	 * The discord token used for the discord bot
	 */
	String token = Config.get("discord.token");

	/**
	 * The JDBC Url used for creating and connecting to the database
	 */
	String jdbcUrl = Config.get("jdbc.url");

	/**
	 * The ID of the guild to make sure the Bot is used on only one guild
	 */
	long guildId = Config.getLong("guild.id");

	/**
	 * The ID of the role which allows to edit entries of groups & users
	 */
	long p2Id = Config.getLong("p2.role.id");

	/**
	 * Additional role (currently) only used for spot token-bucket mechanism
	 */
	long p3Id = Config.getLong("p3.role.id");

	/**
	 * The ID of the role which allows to create & edit entries of groups & users
	 */
	long p4Id = Config.getLong("p4.role.id");

	/**
	 * The ID of the channel where edit requests are sent to in case someone without
	 * sufficient permission tries to do so
	 */
	long requestChannelId = Config.getLong("request.channel.id");

	/**
	 * The ID of the channel where bug reports and other important events are logged
	 */
	long logChannelId = Config.getLong("log.channel.id");

	/**
	 * The color all embeds should have
	 */
	int colorNormal = Integer.parseInt(Config.get("color.normal"), 16);

	/**
	 * The color the request embeds have
	 */
	int colorRequest = Integer.parseInt(Config.get("color.request"), 16);

	/**
	 * The Bot maintenance flag
	 */
	@Setter @NonFinal boolean inMaintenance = false;
}
