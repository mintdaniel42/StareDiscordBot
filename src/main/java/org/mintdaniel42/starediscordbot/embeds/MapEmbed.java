package org.mintdaniel42.starediscordbot.embeds;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.data.entity.MapEntity;
import org.mintdaniel42.starediscordbot.utils.R;

@UtilityClass
public class MapEmbed {
	@Contract(pure = true, value = "_ -> new")
	public @NonNull MessageEmbed of(@NonNull final MapEntity map) {
		return new EmbedBuilder()
				.setTitle(R.Strings.ui("map_details"))
				.addField(R.Strings.ui("the_maps_name"), map.getName(), false)
				.addField(R.Strings.ui("the_maps_release_date"), map.getRelease().toString(), false) // TODO: format date
				.addField(R.Strings.ui("the_maps_difficulty"), map.getDifficulty().toString(), false) // TODO: use strings for difficulty text
				.build();
	}
}
