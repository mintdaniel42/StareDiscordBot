package org.mintdaniel42.starediscordbot.buttons.misc;

import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.compose.button.BaseComposeButton;
import org.mintdaniel42.starediscordbot.compose.button.ButtonContext;
import org.mintdaniel42.starediscordbot.data.Database;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
@Singleton
public final class ApproveButton extends BaseComposeButton {
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
	protected @NonNull MessageEditData compose(@NonNull final ButtonContext context) throws BotException {
		requireButtonPartCount(context, 1);
		database.mergeRequest(Long.parseLong(requireButtonPart(context, 0)));
		return response()
				.addComponent(ActionRow.of(create(-1)))
				.compose();
	}
}
