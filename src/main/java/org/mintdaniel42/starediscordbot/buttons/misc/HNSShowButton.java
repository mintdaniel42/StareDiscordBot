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
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.data.UserModel;
import org.mintdaniel42.starediscordbot.embeds.UserEmbed;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.UUID;

@RequiredArgsConstructor
public final class HNSShowButton implements ButtonAdapter {
	@NonNull private final DatabaseAdapter databaseAdapter;

	@Contract(pure = true, value = "_, _ -> new")
	public static @NonNull Button create(@NonNull final Type type, @NonNull final UUID uuid) {
		return Button.primary(
				"hns:%s:%s".formatted(type.name(), uuid),
				R.Strings.ui(type == Type.more ? "more_info" : "basic_info")
		).withEmoji(R.Emojis.information);
	}

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final ButtonInteractionEvent event) {
		String[] buttonParts = event.getComponentId().split(":");

		final var current = Type.valueOf(buttonParts[1]);
		final var uuid = UUID.fromString(buttonParts[2]);

		if (databaseAdapter.getUser(uuid) instanceof UserModel userModel && userModel.getHnsUser() != null) {
			return interactionHook.editOriginalEmbeds(UserEmbed.of(userModel, current == Type.more ? UserEmbed.Type.HNS_MORE : UserEmbed.Type.HNS))
					.setComponents(ActionRow.of(HNSShowButton.create(current == Type.basic ? Type.more : Type.basic, uuid), GroupButton.create(userModel)));
		} else return interactionHook.editOriginal(R.Strings.ui("this_username_or_entry_does_not_exist"));
	}

	public enum Type {
		basic,
		more
	}
}
