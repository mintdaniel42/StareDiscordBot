package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.buttons.ListButtons;
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.embeds.InfoEmbed;

@RequiredArgsConstructor
public final class InfoCommand implements CommandAdapter {
	@NonNull final DatabaseAdapter databaseAdapter;

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull InteractionHook interactionHook, @NonNull SlashCommandInteractionEvent event) {
		return interactionHook.editOriginalEmbeds(InfoEmbed.of(databaseAdapter))
				.setComponents(ListButtons.createInfo());
	}
}
