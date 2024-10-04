package org.mintdaniel42.starediscordbot.commands.pg;

import io.avaje.inject.RequiresBean;
import io.avaje.inject.RequiresProperty;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.BotConfig;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.data.entity.PGUserEntity;
import org.mintdaniel42.starediscordbot.data.entity.UserEntity;
import org.mintdaniel42.starediscordbot.data.repository.PGUserRepository;
import org.mintdaniel42.starediscordbot.data.repository.ProfileRepository;
import org.mintdaniel42.starediscordbot.data.repository.UserRepository;
import org.mintdaniel42.starediscordbot.embeds.user.pg.PGUserEmbed;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.Permission;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
@RequiresBean(PGCommand.class)
@RequiresProperty(value = "feature.command.pg.add.enabled", equalTo = "true")
@Singleton
public final class PGAddCommand extends BaseComposeCommand {
	@NonNull private final PGUserRepository pgUserRepository;
	@NonNull private final UserRepository userRepository;
	@NonNull private final ProfileRepository profileRepository;

	@Override
	protected @NonNull MessageEditData compose(@NonNull final CommandContext context) throws BotException {
		final var profile = requireProfile(profileRepository, requireStringOption(context, "username"));
		final var pgUser = merge(context, PGUserEntity.builder().uuid(profile.getUuid()));
		final var user = nullableEntity(userRepository, profile.getUuid())
				.orElseGet(() -> UserEntity.builder()
						.uuid(profile.getUuid())
						.build());
		userRepository.upsert(user);
		pgUserRepository.insert(pgUser);
		return response()
				.setContent(R.Strings.ui("the_entry_was_successfully_created"))
				.setEmbeds(PGUserEmbed.of(pgUser, profile, false))
				.build();
	}

	@Inject
	public void register(@NonNull @Named("pg") SlashCommandData command) {
		command.addSubcommands(new SubcommandData("add", R.Strings.ui("add_a_new_partygames_entry"))
				.addOption(OptionType.STRING, "username", R.Strings.ui("minecraft_username"), true, true)
				.addOption(OptionType.NUMBER, "points", R.Strings.ui("points"), true, true)
				.addOption(OptionType.STRING, "rating", R.Strings.ui("rating"))
				.addOption(OptionType.STRING, "joined", R.Strings.ui("joined"))
				.addOption(OptionType.NUMBER, "luck", R.Strings.ui("luck"), false, true)
				.addOption(OptionType.NUMBER, "quota", R.Strings.ui("quota"))
				.addOption(OptionType.NUMBER, "winrate", R.Strings.ui("winrate")));
	}

	@Override
	public @NonNull String getCommandId() {
		return "pg add";
	}

	@Override
	public boolean hasPermission(@NonNull final BotConfig config, @Nullable final Member member) {
		return Permission.hasP4(config, member);
	}
}
