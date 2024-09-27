package org.mintdaniel42.starediscordbot.compose.button;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.buttons.ButtonAdapter;
import org.mintdaniel42.starediscordbot.compose.Composer;
import org.mintdaniel42.starediscordbot.compose.exception.ButtonIncompleteException;
import org.mintdaniel42.starediscordbot.exception.BotException;

import java.util.Optional;
import java.util.function.Supplier;

public abstract class BaseComposeButton extends Composer<ButtonContext> implements ButtonAdapter {
	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final ButtonInteractionEvent event) throws BotException {
		try {
			return interactionHook.editOriginal(compose(new ButtonContext(event)));
		} catch (BotException e) {
			return interactionHook.editOriginal(response()
					.setContent(e.getMessage())
					.build());
		}
	}

	/* ========== BUTTON META ========== */
	protected static boolean requireButtonPartCount(@NonNull final ButtonContext context, final int atLeast) throws ButtonIncompleteException {
		if (context.getButtonPartsLength() < atLeast) {
			throw new ButtonIncompleteException();
		}
		return true;
	}

	protected static @NonNull <T> T requireButtonPartCount(@NonNull final ButtonContext context, final int atLeast, @NonNull final Supplier<T> supplier) throws ButtonIncompleteException {
		if (context.getButtonPartsLength() < atLeast) {
			throw new ButtonIncompleteException();
		}
		return supplier.get();
	}

	/* ========== BUTTON PARTS ========== */
	protected static @NonNull String requireButtonPart(@NonNull final ButtonContext context, final int index) throws ButtonIncompleteException {
		return Optional.ofNullable(context.getButtonPartAt(index))
				.orElseThrow(ButtonIncompleteException::new);
	}
}
