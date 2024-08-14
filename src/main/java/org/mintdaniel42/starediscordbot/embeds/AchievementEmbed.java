package org.mintdaniel42.starediscordbot.embeds;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.mintdaniel42.starediscordbot.data.AchievementModel;
import org.mintdaniel42.starediscordbot.utils.R;

@UtilityClass
public class AchievementEmbed {
	public @NonNull MessageEmbed of(@NonNull final AchievementModel achievementModel) {
		return new EmbedBuilder()
				.addField(R.Strings.ui("achievement_name"), achievementModel.getName(), false)
				.addField(R.Strings.ui("achievement_description"), achievementModel.getDescription(), false)
				.addField(R.Strings.ui("achievement_type"), R.Strings.ui(switch (achievementModel.getType()) {
					case riddle -> "riddle";
					case normal -> "normal";
					case longterm -> "longterm";
				}), false)
				.addField(R.Strings.ui("achievement_points"), String.valueOf(achievementModel.getPoints()), false)
				.build();
	}
}
