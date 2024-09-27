package org.mintdaniel42.starediscordbot.commands.hns.achievements;

import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.BotConfig;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.compose.exception.ComposeException;
import org.mintdaniel42.starediscordbot.data.entity.AchievementEntity;
import org.mintdaniel42.starediscordbot.data.exceptions.DatabaseException;
import org.mintdaniel42.starediscordbot.data.repository.AchievementRepository;
import org.mintdaniel42.starediscordbot.embeds.AchievementEmbed;
import org.mintdaniel42.starediscordbot.utils.Permission;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.UUID;

@RequiredArgsConstructor
@Singleton
public final class AchievementsAddCommand extends BaseComposeCommand {
	@NonNull private final AchievementRepository achievementRepository;
	@NonNull private final BotConfig config;

	@Override
	public @NonNull MessageEditData compose(@NonNull final CommandContext context) throws ComposeException, DatabaseException {
		final var builder = requireStringOption(context, "name", name -> AchievementEntity.builder()
				.uuid(UUID.nameUUIDFromBytes(name.getBytes()))
				.name(name)
		);
		requireStringOption(context, "description", builder::description);
		builder.type(requireStringOption(context, "type", AchievementEntity.Type::valueOf));
		requireIntegerOption(context, "points", builder::points);
		final var achievement = builder.build();
		achievementRepository.insert(achievement);
		return response()
				.setContent(R.Strings.ui("the_achievement_was_successfully_created"))
				.setEmbeds(new AchievementEmbed(achievement, config, 0, 1))
				.build();
	}

	@Override
	public @NonNull String getCommandId() {
		return "hns achievements add";
	}

	@Override
	public boolean hasPermission(@Nullable final Member member) {
		return Permission.hasP4(member);
	}
}
