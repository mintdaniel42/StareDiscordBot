package org.mintdaniel42.starediscordbot.commands.hns.achievements;

import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.mintdaniel42.starediscordbot.BotConfig;
import org.mintdaniel42.starediscordbot.buttons.list.AchievementListButtons;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.data.entity.AchievementEntity;
import org.mintdaniel42.starediscordbot.data.repository.AchievementRepository;
import org.mintdaniel42.starediscordbot.embeds.AchievementEmbed;
import org.mintdaniel42.starediscordbot.exception.BotException;

@RequiredArgsConstructor
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

	@Override
	public @NonNull String getCommandId() {
		return "hns achievements list";
	}
}
