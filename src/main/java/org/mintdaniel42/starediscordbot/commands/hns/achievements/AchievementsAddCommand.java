package org.mintdaniel42.starediscordbot.commands.hns.achievements;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.commands.CommandAdapter;
import org.mintdaniel42.starediscordbot.data.AchievementModel;
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.embeds.AchievementEmbed;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.UUID;

@RequiredArgsConstructor
public final class AchievementsAddCommand implements CommandAdapter {
	@NonNull private final DatabaseAdapter databaseAdapter;

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("name") instanceof final OptionMapping nameMapping &&
				event.getOption("description") instanceof final OptionMapping descriptionMapping &&
				event.getOption("type") instanceof final OptionMapping typeMapping &&
				event.getOption("points") instanceof final OptionMapping pointsMapping) {

			final var model = AchievementModel.builder()
					.uuid(UUID.nameUUIDFromBytes(nameMapping.getAsString()
							.toLowerCase()
							.getBytes()))
					.name(nameMapping.getAsString())
					.description(descriptionMapping.getAsString())
					.type(AchievementModel.Type.valueOf(typeMapping.getAsString()))
					.points(pointsMapping.getAsInt())
					.build();

			return interactionHook.editOriginal(switch (databaseAdapter.addAchievement(model)) {
				case SUCCESS -> R.Strings.ui("the_achievement_was_successfully_created");
				case DUPLICATE -> R.Strings.ui("this_achievement_already_exists");
				case ERROR -> R.Strings.ui("the_achievement_could_not_be_added");
			}).setEmbeds(AchievementEmbed.of(model, 0, 1));
		} else return interactionHook.editOriginal(R.Strings.ui("your_command_was_incomplete"));
	}
}
