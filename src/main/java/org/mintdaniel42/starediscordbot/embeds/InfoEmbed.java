package org.mintdaniel42.starediscordbot.embeds;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.mintdaniel42.starediscordbot.data.entity.MetaDataEntity;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

@UtilityClass
public class InfoEmbed {
	public MessageEmbed of(@NonNull final MetaDataEntity.Version version, final int usernameCount, final int hnsCount, final int pgCount, final int groupCount, final int spotCount) {
		return new EmbedBuilder()
				.setColor(Options.getColorNormal())
				.addField(R.Strings.ui("version"), "%s - \"%s\"".formatted(version.name().replace('_', '.'), version.getTitle()), false)
				.addField(R.Strings.ui("uptime"), getUptime(), false)
				.addField(R.Strings.ui("count_of_stored_usernames"), String.valueOf(usernameCount), false)
				.addField(R.Strings.ui("hide_n_seek_entry_count"), String.valueOf(hnsCount), false)
				.addField(R.Strings.ui("partygames_entry_count"), String.valueOf(pgCount), false)
				.addField(R.Strings.ui("group_count"), String.valueOf(groupCount), false)
				.addField(R.Strings.ui("spot_count"), String.valueOf(spotCount), false)
				.build();
	}

	private String getUptime() {
		final var millis = ManagementFactory.getRuntimeMXBean().getUptime();

		final var hours = TimeUnit.MILLISECONDS.toHours(millis);
		final var minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(hours);
		final var seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minutes);

		return String.format("%02dh:%02dm:%02ds", hours, minutes, seconds);
	}
}
