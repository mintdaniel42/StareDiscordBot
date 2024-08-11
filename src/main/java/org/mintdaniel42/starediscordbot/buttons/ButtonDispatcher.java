package org.mintdaniel42.starediscordbot.buttons;

import lombok.NonNull;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.utils.Permissions;

public class ButtonDispatcher extends ListenerAdapter {
	@NonNull private final ButtonAdapter approveButton;
	@NonNull private final ButtonAdapter groupButton;

	public ButtonDispatcher(@NonNull final DatabaseAdapter databaseAdapter) {
		approveButton = new ApproveButton(databaseAdapter);
		groupButton = new GroupButton(databaseAdapter);
	}

	@Override
	public void onButtonInteraction(@NonNull final ButtonInteractionEvent event) {
		event.deferReply().queue(interactionHook -> handleButton(event)
				.handle(interactionHook, event)
				.queue());
	}

	private @NonNull ButtonAdapter handleButton(@NonNull final ButtonInteractionEvent event) {
		return switch (event.getComponentId().split(":")) {
			case String[] b when b.length == 2 &&
					b[0].equals("approve") &&
					Permissions.canEdit(event.getMember()) -> approveButton;
			case String[] b when b.length == 2 &&
					b[0].equals("group") &&
					Permissions.canView() -> groupButton;
			default -> null;
		};
	}
}
