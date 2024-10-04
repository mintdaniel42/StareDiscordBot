package org.mintdaniel42.starediscordbot.commands.hns.achievements;

import io.avaje.inject.RequiresBean;
import io.avaje.inject.RequiresProperty;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.BotConfig;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.data.entity.AchievementEntity;
import org.mintdaniel42.starediscordbot.data.repository.AchievementRepository;
import org.mintdaniel42.starediscordbot.embeds.AchievementEmbed;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.Permission;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.UUID;

@RequiredArgsConstructor
@RequiresBean(AchievementsGroup.class)
@RequiresProperty(value = "feature.command.hns.achievements.add.enabled", equalTo = "true")
@Singleton
public final class AchievementsAddCommand extends BaseComposeCommand {
	@NonNull private final AchievementRepository achievementRepository;
	@NonNull private final BotConfig config;

	@Override
	public @NonNull MessageEditData compose(@NonNull final CommandContext context) throws BotException {
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

	@Inject
	public void register(@NonNull @Named("hns achievements") SubcommandGroupData group) {
		group.addSubcommands(new SubcommandData("add", R.Strings.ui("add_an_achievement"))
				.addOption(OptionType.STRING, "name", R.Strings.ui("achievement_name"), true)
				.addOption(OptionType.STRING, "description", R.Strings.ui("achievement_description"), true)
				.addOptions(new OptionData(OptionType.STRING, "type", R.Strings.ui("achievement_type"), true)
						.addChoice(R.Strings.ui("riddle"), AchievementEntity.Type.riddle.name())
						.addChoice(R.Strings.ui("normal"), AchievementEntity.Type.normal.name())
						.addChoice(R.Strings.ui("longterm"), AchievementEntity.Type.longterm.name()))
				.addOption(OptionType.STRING, "points", R.Strings.ui("achievement_points"), true)
		);
	}

	@Override
	public @NonNull String getCommandId() {
		return "hns achievements add";
	}

	@Override
	public boolean hasPermission(@NonNull final BotConfig config, @Nullable final Member member) {
		return Permission.hasP4(config, member);
	}
}
