package org.mintdaniel42.starediscordbot.commands.hns.achievements;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.buttons.list.AchievementListButtons;
import org.mintdaniel42.starediscordbot.commands.CommandAdapter;
import org.mintdaniel42.starediscordbot.data.AchievementModel;
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.embeds.AchievementEmbed;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.List;

@RequiredArgsConstructor
public class AchievementsListCommand implements CommandAdapter {
	@NonNull private final DatabaseAdapter databaseAdapter;

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		final AchievementModel.Type type;
		final int points;
		final int page;
		if (event.getOption("type") instanceof final OptionMapping typeMapping) {
			type = AchievementModel.Type.valueOf(typeMapping.getAsString());
		} else type = null;
		if (event.getOption("points") instanceof final OptionMapping pointsMapping) {
			points = pointsMapping.getAsInt();
		} else points = -1;
		if (event.getOption("page") instanceof final OptionMapping pageMapping) {
			page = pageMapping.getAsInt();
		} else page = 1;
		if (databaseAdapter.getAchievements(type, points) instanceof final List<AchievementModel> achievementModels) {
			System.out.println(page);
			System.out.println(achievementModels.size());
			if (page <= achievementModels.size()) {
				return interactionHook.editOriginalEmbeds(AchievementEmbed.of(achievementModels.get(page - 1)))
						.setComponents(AchievementListButtons.create(type, points, page - 1, achievementModels.size()));
			} else return interactionHook.editOriginal(R.Strings.ui("this_page_does_not_exist"));
		} else return interactionHook.editOriginal(R.Strings.ui("no_entries_available"));
	}
}
