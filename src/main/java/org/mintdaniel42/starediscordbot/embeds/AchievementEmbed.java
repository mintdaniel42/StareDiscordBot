package org.mintdaniel42.starediscordbot.embeds;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.mintdaniel42.starediscordbot.BotConfig;
import org.mintdaniel42.starediscordbot.data.entity.AchievementEntity;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.List;

public final class AchievementEmbed extends MessageEmbed {
	public AchievementEmbed(@NonNull final AchievementEntity achievement, @NonNull final BotConfig config, final int page, final int maxPages) {
		super(
				null,
				R.Strings.ui("achievements"),
				R.Strings.ui("page_s_of_s", page + 1, maxPages),
				EmbedType.RICH,
				null,
				config.getColorNormal(),
				null,
				null,
				null,
				null,
				null,
				null,
				createFields(achievement)
		);
	}

	private static @NonNull List<Field> createFields(@NonNull final AchievementEntity achievement) {
		return List.of(
				new Field(R.Strings.ui("achievement_name"), achievement.getName(), false),
				new Field(R.Strings.ui("achievement_description"), achievement.getDescription(), false),
				new Field(R.Strings.ui("achievement_type"), R.Strings.ui(switch (achievement.getType()) {
					case riddle -> "riddle";
					case normal -> "normal";
					case longterm -> "longterm";
				}), false),
				new Field(R.Strings.ui("achievement_points"), String.valueOf(achievement.getPoints()), false)
		);
	}
}
