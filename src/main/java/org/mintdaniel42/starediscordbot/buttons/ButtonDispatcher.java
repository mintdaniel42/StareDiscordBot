package org.mintdaniel42.starediscordbot.buttons;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.buttons.list.*;
import org.mintdaniel42.starediscordbot.buttons.misc.ApproveButton;
import org.mintdaniel42.starediscordbot.buttons.misc.GroupButton;
import org.mintdaniel42.starediscordbot.buttons.misc.HNSShowButton;
import org.mintdaniel42.starediscordbot.buttons.misc.TutorialSuggestionButtons;
import org.mintdaniel42.starediscordbot.data.Database;
import org.mintdaniel42.starediscordbot.embeds.ErrorEmbed;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.Permissions;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
@Slf4j
public final class ButtonDispatcher extends ListenerAdapter implements ButtonAdapter {
	@NonNull private final Database database;

	@Override
	public void onButtonInteraction(@NonNull final ButtonInteractionEvent event) {
		//#if dev
		if (event.getMember() instanceof Member member) {
			log.info(R.Strings.log("button_s_pressed_by_user_s",
					event.getComponentId(),
					member.getEffectiveName()));
		}
		//#endif

		event.deferEdit().queue(interactionHook -> {
			try {
				handleButton(event)
						.handle(interactionHook, event)
						.queue();
			} catch (Exception e) {
				new ErrorHandler(e).handle(interactionHook, event);
			}
		});
	}

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final ButtonInteractionEvent event) {
		if (Options.isInMaintenance())
			return interactionHook.editOriginal((R.Strings.ui("the_bot_is_currently_in_maintenance_mode")));
		else return interactionHook.editOriginal(R.Strings.ui("you_do_not_have_the_permission_to_use_this_button"));
	}

	private @NonNull ButtonAdapter handleButton(@NonNull final ButtonInteractionEvent event) {
		return switch (event.getComponentId().split(":")) {
			case String[] b when b.length == 2 && b[0].equals("approve") && Permissions.edit(event.getMember()) ->
					new ApproveButton(database);
			case String[] b when b.length == 2 && b[0].equals("group") && Permissions.view() ->
					new GroupButton(database.getGroupRepository(), database.getHnsUserRepository(), database.getUserRepository(), database.getUsernameRepository());
			case String[] b when b.length == 3 && b[0].equals("hns") && Permissions.view() ->
					new HNSShowButton(database.getHnsUserRepository(), database.getUserRepository(), database.getUsernameRepository(), database.getGroupRepository());
			case String[] b when b.length == 3 && b[0].equals("tutorial") && b[2].equals("suggestion") && Permissions.view() ->
					new TutorialSuggestionButtons();
			case String[] b when b.length == 3 && b[0].equals("tutorial") && b[2].equals("list") && Permissions.view() ->
					new TutorialListButtons();
			case String[] b when b.length == 3 && b[0].equals("list") && b[1].equals("pg") && Permissions.view() ->
					new PGListButtons(database.getPgUserRepository(), database.getUsernameRepository());
			case String[] b when b.length == 3 && b[0].equals("list") && b[1].equals("hns") && Permissions.view() ->
					new HNSListButtons(database.getHnsUserRepository(), database.getUsernameRepository());
			case String[] b when b.length == 3 && b[0].equals("group") && Permissions.view() ->
					new GroupListButtons(database.getGroupRepository(), database.getHnsUserRepository(), database.getUserRepository(), database.getUsernameRepository());
			case String[] b when b.length == 4 && b[0].equals("achievement") && Permissions.view() ->
					new AchievementListButtons(database.getAchievementRepository());
			default -> this;
		};
	}

	private record ErrorHandler(@NonNull Exception exception) implements ButtonAdapter {
		@Override
		public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final ButtonInteractionEvent event) {
			if (event.getGuild() instanceof final Guild guild) {
				if (guild.getTextChannelById(Options.getLogChannelId()) instanceof final TextChannel channel) {
					channel.sendMessageEmbeds(ErrorEmbed.of(event.getComponentId(), exception)).queue();
				}
			}
			return interactionHook.editOriginal(R.Strings.ui("an_error_occurred_the_developer_has_been_notified"));
		}
	}
}
