package org.mintdaniel42.starediscordbot.buttons.misc;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.buttons.ButtonAdapter;
import org.mintdaniel42.starediscordbot.data.Database;
import org.mintdaniel42.starediscordbot.utils.R;
import org.mintdaniel42.starediscordbot.utils.Status;

@RequiredArgsConstructor
public final class ApproveButton implements ButtonAdapter {
	@NonNull private final Database database;

	@Contract(pure = true, value = "_ -> new")
	public static @NonNull Button create(final long id) {
		return Button.success(
						"approve:%s".formatted(id),
						R.Strings.ui("approve_this_change"))
				.withEmoji(R.Emojis.approve)
				.withDisabled(id == -1);
	}

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final ButtonInteractionEvent event) {
		if (database.mergeRequest(Long.parseLong(event.getComponentId().split(":")[1])).equals(Status.SUCCESS)) {
			return interactionHook.editOriginalComponents(ActionRow.of(create(-1)));
		} else return interactionHook.editOriginal(R.Strings.ui("request_could_not_be_merged"));
	}
}
