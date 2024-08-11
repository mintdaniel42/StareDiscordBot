package org.mintdaniel42.starediscordbot.buttons;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
public final class ApproveButton implements ButtonAdapter {
	@NonNull private final DatabaseAdapter databaseAdapter;

	@Contract(pure = true, value = "_ -> new")
	public static @NonNull ActionRow create(final long id) {
		return ActionRow.of(Button.success(
						"approve:%s".formatted(id),
						R.Strings.ui("approve_this_change")
						).withEmoji(R.Emojis.approve)
						.withDisabled(id == -1)
		);
	}

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final ButtonInteractionEvent event) {
		if (databaseAdapter.mergeRequest(Long.parseLong(event.getComponentId().split(":")[1]))) {
			return interactionHook.editOriginalComponents(create(-1));
		} else return interactionHook.editOriginal(R.Strings.ui("request_could_not_be_merged"));
	}
}
