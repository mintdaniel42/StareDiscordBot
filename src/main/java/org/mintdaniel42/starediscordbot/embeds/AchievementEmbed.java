package org.mintdaniel42.starediscordbot.embeds;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.mintdaniel42.starediscordbot.data.entity.AchievementEntity;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

@UtilityClass
public class AchievementEmbed {
	public @NonNull MessageEmbed of(@NonNull final AchievementEntity achievement, final int pages, final int maxPages) {
		return new EmbedBuilder()
				.setTitle(R.Strings.ui("achievements"))
				.setDescription(R.Strings.ui("page_s_of_s", pages, maxPages))
				.setColor(Options.getColorNormal())
				.addField(R.Strings.ui("achievement_name"), achievement.getName(), false)
				.addField(R.Strings.ui("achievement_description"), achievement.getDescription(), false)
				.addField(R.Strings.ui("achievement_type"), R.Strings.ui(switch (achievement.getType()) {
					case riddle -> "riddle";
					case normal -> "normal";
					case longterm -> "longterm";
				}), false)
				.addField(R.Strings.ui("achievement_points"), String.valueOf(achievement.getPoints()), false)
				.build();
	}
}
