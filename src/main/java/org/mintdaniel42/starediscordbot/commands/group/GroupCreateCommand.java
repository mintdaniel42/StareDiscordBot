package org.mintdaniel42.starediscordbot.commands.group;

import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.commands.CommandAdapter;
import org.mintdaniel42.starediscordbot.data.entity.GroupEntity;
import org.mintdaniel42.starediscordbot.data.repository.GroupRepository;
import org.mintdaniel42.starediscordbot.data.repository.HNSUserRepository;
import org.mintdaniel42.starediscordbot.data.repository.UserRepository;
import org.mintdaniel42.starediscordbot.data.repository.UsernameRepository;
import org.mintdaniel42.starediscordbot.embeds.GroupEmbed;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.UUID;

@RequiredArgsConstructor
@Singleton
public final class GroupCreateCommand implements CommandAdapter {
	@NonNull private final GroupRepository groupRepository;
	@NonNull private final HNSUserRepository hnsUserRepository;
	@NonNull private final UserRepository userRepository;
	@NonNull private final UsernameRepository usernameRepository;

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("tag") instanceof final OptionMapping tagMapping &&
				event.getOption("name") instanceof final OptionMapping nameMapping &&
				event.getOption("leader") instanceof final OptionMapping leaderMapping &&
				event.getOption("relation") instanceof final OptionMapping relationMapping) {
			if (MCHelper.getUuid(usernameRepository, leaderMapping.getAsString()) instanceof final UUID uuid) {
				final var group = GroupEntity.builder()
						.tag(tagMapping.getAsString())
						.name(nameMapping.getAsString())
						.leader(uuid)
						.relation(GroupEntity.Relation.valueOf(relationMapping.getAsString()))
						.build();

				return interactionHook.editOriginal(switch (groupRepository.insert(group)) {
					case SUCCESS -> R.Strings.ui("the_group_was_successfully_created");
					case DUPLICATE -> R.Strings.ui("this_group_already_exists");
					case ERROR -> R.Strings.ui("the_group_could_not_be_created");
				}).setEmbeds(GroupEmbed.of(group, userRepository, hnsUserRepository, usernameRepository, 0, false));
			} else return interactionHook.editOriginal(R.Strings.ui("this_username_does_not_exist"));
		} else return interactionHook.editOriginal(R.Strings.ui("your_command_was_incomplete"));
	}
}
