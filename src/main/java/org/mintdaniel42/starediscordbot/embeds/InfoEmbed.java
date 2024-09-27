package org.mintdaniel42.starediscordbot.embeds;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.mintdaniel42.starediscordbot.BotConfig;
import org.mintdaniel42.starediscordbot.data.entity.MetaDataEntity;
import org.mintdaniel42.starediscordbot.utils.R;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InfoEmbed extends MessageEmbed {
	public InfoEmbed(@NonNull final BotConfig config, @NonNull final MetaDataEntity.Version version, final int usernameCount, final int hnsCount, final int pgCount, final int groupCount, final int spotCount) {
		super(
				null,
				null,
				null,
				EmbedType.RICH,
				null,
				config.getColorNormal(),
				null,
				null,
				null,
				null,
				null,
				null,
				createFields(version, usernameCount, hnsCount, pgCount, groupCount, spotCount)
		);
	}

	private static @NonNull String getUptime() {
		final var millis = ManagementFactory.getRuntimeMXBean().getUptime();
		final var hours = TimeUnit.MILLISECONDS.toHours(millis);
		final var minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(hours);
		final var seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minutes);
		return String.format("%02dh:%02dm:%02ds", hours, minutes, seconds);
	}

	private static @NonNull List<Field> createFields(@NonNull final MetaDataEntity.Version version, final int usernameCount, final int hnsCount, final int pgCount, final int groupCount, final int spotCount) {
		return List.of(
				new Field(R.Strings.ui("version"), "%s - \"%s\"".formatted(version.name().replace('_', '.'), version.getTitle()), false),
				new Field(R.Strings.ui("uptime"), getUptime(), false),
				new Field(R.Strings.ui("count_of_stored_usernames"), String.valueOf(usernameCount), false),
				new Field(R.Strings.ui("hide_n_seek_entry_count"), String.valueOf(hnsCount), false),
				new Field(R.Strings.ui("partygames_entry_count"), String.valueOf(pgCount), false),
				new Field(R.Strings.ui("group_count"), String.valueOf(groupCount), false),
				new Field(R.Strings.ui("spot_count"), String.valueOf(spotCount), false)
		);
	}
}
