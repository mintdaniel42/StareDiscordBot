package org.mintdaniel42.starediscordbot.embeds;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.utils.R;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;

@UtilityClass
public class InfoEmbed {
	public MessageEmbed create(@NonNull final DatabaseAdapter databaseAdapter) {
		return new EmbedBuilder()
				.addField(R.Strings.ui("uptime"), getUptime(), false)
				.addField(R.Strings.ui("count_of_stored_usernames"), String.valueOf(databaseAdapter.getUsernameCount()), false)
				.addField(R.Strings.ui("hide_n_seek_entry_count"), String.valueOf(databaseAdapter.getHnsCount()), false)
				.addField(R.Strings.ui("partygames_entry_count"), String.valueOf(databaseAdapter.getPgCount()), false)
				.build();
	}

	private String getUptime() {
		final var millis = ManagementFactory.getRuntimeMXBean().getUptime();
		return (new SimpleDateFormat("mm:ss:SSS")).format(new Date(millis));
	}
}
