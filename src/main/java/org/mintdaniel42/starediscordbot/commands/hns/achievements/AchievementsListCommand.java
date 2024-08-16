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
import org.mintdaniel42.starediscordbot.data.entity.AchievementEntity;
import org.mintdaniel42.starediscordbot.data.repository.AchievementRepository;
import org.mintdaniel42.starediscordbot.embeds.AchievementEmbed;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
public final class AchievementsListCommand implements CommandAdapter {
	@NonNull private final AchievementRepository achievementRepository;

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		final AchievementEntity.Type type;
		final int points;
		final int page;
		if (event.getOption("type") instanceof final OptionMapping typeMapping) {
			type = AchievementEntity.Type.valueOf(typeMapping.getAsString());
		} else type = null;
		if (event.getOption("points") instanceof final OptionMapping pointsMapping) {
			points = pointsMapping.getAsInt();
		} else points = -1;
		if (event.getOption("page") instanceof final OptionMapping pageMapping) {
			page = pageMapping.getAsInt();
		} else page = 1;
		final var achievements = achievementRepository.selectByTypeAndPoints(type, points);
		if (page <= achievements.size()) {
			return interactionHook.editOriginalEmbeds(AchievementEmbed.of(achievements.get(page - 1), page, achievements.size()))
					.setComponents(AchievementListButtons.create(type, points, page - 1, achievements.size()));
		} else return interactionHook.editOriginal(R.Strings.ui("this_page_does_not_exist"));
	}
}
