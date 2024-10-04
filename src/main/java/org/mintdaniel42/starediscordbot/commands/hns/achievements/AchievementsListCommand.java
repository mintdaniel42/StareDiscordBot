package org.mintdaniel42.starediscordbot.commands.hns.achievements;

import io.avaje.inject.RequiresBean;
import io.avaje.inject.RequiresProperty;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.mintdaniel42.starediscordbot.BotConfig;
import org.mintdaniel42.starediscordbot.buttons.list.AchievementListButtons;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.data.entity.AchievementEntity;
import org.mintdaniel42.starediscordbot.data.repository.AchievementRepository;
import org.mintdaniel42.starediscordbot.embeds.AchievementEmbed;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
@RequiresBean(AchievementsGroup.class)
@RequiresProperty(value = "feature.command.hns.achievements.list.enabled", equalTo = "true")
@Singleton
public final class AchievementsListCommand extends BaseComposeCommand {
	@NonNull private final AchievementRepository achievementRepository;
	@NonNull private final BotConfig config;

	@Override
	public @NonNull MessageEditData compose(@NonNull final CommandContext context) throws BotException {
		final var type = nullableStringOption(context, "type", AchievementEntity.Type::valueOf).orElse(null);
		final int points = nullableIntegerOption(context, "type").orElse(-1);
		final int page = nullableIntegerOption(context, "page").orElse(1) - 1;
		final var achievements = achievementRepository.selectByTypeAndPoints(type, points);
		requireBounds(0, page, achievements.size());
		return response()
				.setEmbeds(new AchievementEmbed(achievements.get(page), config, page, achievements.size()))
				.setComponents(AchievementListButtons.create(type, points, page, achievements.size()))
				.build();
	}

	@Inject
	public void register(@NonNull @Named("hns achievements") SubcommandGroupData group) {
		group.addSubcommands(new SubcommandData("list", R.Strings.ui("show_achievements"))
				.addOption(OptionType.STRING, "points", R.Strings.ui("filter_by_achievement_points"), false)
				.addOption(OptionType.INTEGER, "page", R.Strings.ui("page"), false, true)
				.addOptions(new OptionData(OptionType.STRING, "type", R.Strings.ui("filter_by_achievement_type"))
						.addChoice(R.Strings.ui("riddle"), AchievementEntity.Type.riddle.name())
						.addChoice(R.Strings.ui("normal"), AchievementEntity.Type.normal.name())
						.addChoice(R.Strings.ui("longterm"), AchievementEntity.Type.longterm.name()))
		);
	}

	@Override
	public @NonNull String getCommandId() {
		return "hns achievements list";
	}
}
