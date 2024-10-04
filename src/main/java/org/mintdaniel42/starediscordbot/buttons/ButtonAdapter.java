package org.mintdaniel42.starediscordbot.buttons;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.bucket.RateLimited;
import org.mintdaniel42.starediscordbot.exception.BotException;

/**
 * Implementing this interface is equivalent to implementing the logic for a button.
 * It has to be registered in {@link ButtonDispatcher}
 */
@FunctionalInterface
public interface ButtonAdapter extends RateLimited {
	/**
	 * @param interactionHook this is the {@link InteractionHook} which has to be used for responses
	 * @param event           contains read-only data like the component id, should not be used for responding
	 * @return the {@link WebhookMessageEditAction} returned by calling an interactionHook edit action
	 */
	@NonNull
	WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final ButtonInteractionEvent event) throws BotException;

	/**
	 * If pressing a button should result in editing the original message or creating a new one
	 *
	 * @return usually true
	 */
	default boolean sameMessage() {
		return true;
	}
}
