package org.mintdaniel42.starediscordbot.commands.pg;

import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
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

	@Override
	public @NonNull String getCommandId() {
		return "pg add";
	}

	@Override
	public boolean hasPermission(@NonNull final BotConfig config, @Nullable final Member member) {
		return Permission.hasP4(config, member);
	}
}
