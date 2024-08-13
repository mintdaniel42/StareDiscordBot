package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;

/**
 * Implementing this interface is equivalent to implementing the logic for a command.
 * It has to be registered in {@link CommandDispatcher}
 */
@FunctionalInterface
public interface CommandAdapter {
	/**
	 * @param interactionHook this is the {@link InteractionHook} which has to be used for responses
	 * @param event           contains read-only data like {@link OptionMapping}, should not be used for responding
	 * @return the {@link WebhookMessageEditAction} returned by calling an interactionHook edit action
	 */
	@NonNull
	WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event);
}
