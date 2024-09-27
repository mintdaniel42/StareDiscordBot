package org.mintdaniel42.starediscordbot.commands.hns;

import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.compose.exception.ComposeException;
import org.mintdaniel42.starediscordbot.data.entity.HNSUserEntity;
import org.mintdaniel42.starediscordbot.data.entity.UserEntity;
import org.mintdaniel42.starediscordbot.data.exceptions.DatabaseException;
import org.mintdaniel42.starediscordbot.data.repository.HNSUserRepository;
import org.mintdaniel42.starediscordbot.data.repository.ProfileRepository;
import org.mintdaniel42.starediscordbot.data.repository.UserRepository;
import org.mintdaniel42.starediscordbot.embeds.user.hns.HNSFullUserEmbed;
import org.mintdaniel42.starediscordbot.utils.Permission;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
@Singleton
public final class HNSAddCommand extends BaseComposeCommand {
	@NonNull private final HNSUserRepository hnsUserRepository;
	@NonNull private final UserRepository userRepository;
	@NonNull private final ProfileRepository profileRepository;

	@Override
	protected @NonNull MessageEditData compose(@NonNull final CommandContext context) throws ComposeException, DatabaseException {
		requireOptionCount(context, 2);
		final var profile = requireProfile(profileRepository, requireStringOption(context, "username"));
		final var hnsUser = HNSUserEntity.merge(context.getOptions(), HNSUserEntity.builder().uuid(profile.getUuid()));
		final var user = nullableEntity(userRepository, profile.getUuid())
				.orElseGet(() -> UserEntity.builder()
						.uuid(profile.getUuid())
						.build());
		userRepository.upsert(user);
		hnsUserRepository.insert(hnsUser);
		return response()
				.setContent(R.Strings.ui("the_entry_was_successfully_created"))
				.setEmbeds(HNSFullUserEmbed.of(hnsUser, profile, false))
				.build();
	}

	@Override
	public @NonNull String getCommandId() {
		return "hns add";
	}

	@Override
	public boolean hasPermission(@Nullable final Member member) {
		return Permission.hasP4(member);
	}
}
