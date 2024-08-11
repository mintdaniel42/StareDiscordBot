package org.mintdaniel42.starediscordbot.buttons;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.Permissions;
import org.mintdaniel42.starediscordbot.utils.R;

@Slf4j
public class ButtonDispatcher extends ListenerAdapter implements ButtonAdapter {
	@NonNull private final ButtonAdapter approveButton;
	@NonNull private final ButtonAdapter groupButton;
	@NonNull private final ButtonAdapter hnsShowButton;

	public ButtonDispatcher(@NonNull final DatabaseAdapter databaseAdapter) {
		approveButton = new ApproveButton(databaseAdapter);
		groupButton = new GroupButton(databaseAdapter);
		hnsShowButton = new HNSShowButton(databaseAdapter);
	}

	@Override
	public void onButtonInteraction(@NonNull final ButtonInteractionEvent event) {
		//#if dev
		if (event.getMember() instanceof Member member) {
			log.info(R.Strings.log("button_s_pressed_by_user_s",
					event.getComponentId(),
					member.getEffectiveName()));
		}
		//#endif

		event.deferReply().queue(interactionHook -> handleButton(event)
				.handle(interactionHook, event)
				.queue());
	}

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final ButtonInteractionEvent event) {
		if (Options.isInMaintenance())
			return interactionHook.editOriginal((R.Strings.ui("the_bot_is_currently_in_maintenance_mode")));
		else return interactionHook.editOriginal(R.Strings.ui("you_do_not_have_the_permission_to_use_this_button"));
	}

	private @NonNull ButtonAdapter handleButton(@NonNull final ButtonInteractionEvent event) {
		return switch (event.getComponentId().split(":")) {
			case String[] b when b.length == 2 &&
					b[0].equals("approve") &&
					Permissions.canEdit(event.getMember()) -> approveButton;
			case String[] b when b.length == 2 &&
					b[0].equals("group") &&
					Permissions.canView() -> groupButton;
			case String[] b when b.length == 3 &&
					b[0].equals("hns") &&
					Permissions.canView() -> hnsShowButton;
			default -> this;
		};
	}
}