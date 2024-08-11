package org.mintdaniel42.starediscordbot.commands.group;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.commands.CommandAdapter;
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.data.GroupModel;
import org.mintdaniel42.starediscordbot.embeds.GroupEmbed;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.UUID;

@RequiredArgsConstructor
public class GroupCreateCommand implements CommandAdapter {
	@NonNull private final DatabaseAdapter databaseAdapter;

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("tag") instanceof final OptionMapping tagMapping &&
				event.getOption("name") instanceof final OptionMapping nameMapping &&
				event.getOption("leader") instanceof final OptionMapping leaderMapping &&
				event.getOption("relation") instanceof final OptionMapping relationMapping) {
			if (!databaseAdapter.hasGroup(tagMapping.getAsString())) {
				if (MCHelper.getUuid(databaseAdapter, leaderMapping.getAsString()) instanceof final UUID uuid) {
					GroupModel.GroupModelBuilder builder = GroupModel.builder();
					builder.tag(tagMapping.getAsString());
					builder.name(nameMapping.getAsString());
					builder.leader(uuid);
					builder.relation(GroupModel.Relation.valueOf(relationMapping.getAsString()));

					GroupModel groupModel = builder.build();
					if (databaseAdapter.addGroup(groupModel)) {
						return interactionHook.editOriginal(R.Strings.ui("the_group_was_successfully_created"))
								.setEmbeds(GroupEmbed.of(databaseAdapter, groupModel, 0));
					} else return interactionHook.editOriginal(R.Strings.ui("the_group_could_not_be_created"));
				} else return interactionHook.editOriginal(R.Strings.ui("this_username_does_not_exist"));
			} else return interactionHook.editOriginal(R.Strings.ui("this_group_already_exists"));
		} else return interactionHook.editOriginal(R.Strings.ui("your_command_was_incomplete"));
	}
}
