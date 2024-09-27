package org.mintdaniel42.starediscordbot.commands.hns;

import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.mintdaniel42.starediscordbot.buttons.misc.GroupButton;
import org.mintdaniel42.starediscordbot.buttons.misc.HNSShowButton;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.compose.exception.ComposeException;
import org.mintdaniel42.starediscordbot.data.exceptions.DatabaseException;
import org.mintdaniel42.starediscordbot.data.repository.GroupRepository;
import org.mintdaniel42.starediscordbot.data.repository.HNSUserRepository;
import org.mintdaniel42.starediscordbot.data.repository.ProfileRepository;
import org.mintdaniel42.starediscordbot.data.repository.UserRepository;
import org.mintdaniel42.starediscordbot.embeds.user.hns.HNSMoreUserEmbed;

@RequiredArgsConstructor
@Singleton
public final class HNSShowMoreCommand extends BaseComposeCommand {
	@NonNull private final HNSUserRepository hnsUserRepository;
	@NonNull private final UserRepository userRepository;
	@NonNull private final ProfileRepository profileRepository;
	@NonNull private final GroupRepository groupRepository;

	@Override
	protected @NonNull MessageEditData compose(@NonNull final CommandContext context) throws ComposeException, DatabaseException {
		final var profile = requireProfile(profileRepository, requireStringOption(context, "username"));
		final var hnsUser = requireEntity(hnsUserRepository, profile.getUuid());
		final var user = requireEntity(userRepository, profile.getUuid());
		final var groupOptional = nullableEntity(groupRepository, user.getGroupTag());
		return response()
				.setEmbeds(HNSMoreUserEmbed.of(hnsUser, user, groupOptional.orElse(null), profile, false))
				.setComponents(ActionRow.of(HNSShowButton.create(HNSShowButton.Type.basic, profile.getUuid()),
						groupOptional.map(GroupButton::create)
								.orElseGet(GroupButton::disabled)))
				.build();
	}

	@Override
	public @NonNull String getCommandId() {
		return "hns showmore";
	}
}
