package org.mintdaniel42.starediscordbot.buttons;

import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.mintdaniel42.starediscordbot.BotConfig;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.buttons.list.*;
import org.mintdaniel42.starediscordbot.buttons.misc.ApproveButton;
import org.mintdaniel42.starediscordbot.buttons.misc.GroupButton;
import org.mintdaniel42.starediscordbot.buttons.misc.HNSShowButton;
import org.mintdaniel42.starediscordbot.buttons.misc.TutorialSuggestionButtons;
import org.mintdaniel42.starediscordbot.compose.button.BaseComposeButton;
import org.mintdaniel42.starediscordbot.compose.button.ButtonContext;
import org.mintdaniel42.starediscordbot.embeds.ErrorEmbed;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.Permission;
import org.mintdaniel42.starediscordbot.utils.R;

import java.time.Duration;
import java.util.Objects;

@RequiredArgsConstructor
@Singleton
@Slf4j
public final class ButtonDispatcher extends ListenerAdapter implements ButtonAdapter {
	@NonNull private final ApproveButton approveButton;
	@NonNull private final GroupButton groupButton;
	@NonNull private final HNSShowButton hnsShowButton;
	@NonNull private final TutorialSuggestionButtons tutorialSuggestionButtons;
	@NonNull private final TutorialListButtons tutorialListButtons;
	@NonNull private final PGListButtons pgListButtons;
	@NonNull private final HNSListButtons hnsListButtons;
	@NonNull private final GroupListButtons groupListButtons;
	@NonNull private final AchievementListButtons achievementListButtons;
	@NonNull private final BotConfig config;

	@Override
	public void onButtonInteraction(@NonNull final ButtonInteractionEvent event) {
		if (!BuildConfig.production && event.getMember() instanceof Member member) {
			log.info(R.Strings.log("button_s_pressed_by_user_s",
					event.getComponentId(),
					member.getEffectiveName()));
		}

		final var adapter = dispatch(event);
		(adapter.sameMessage() ? event.deferEdit() : event.deferReply(adapter.isPublicResponseRestricted()))
				.queue(interactionHook -> {
					try {
						if (adapter.getPool()
								.getBucket(Objects.requireNonNull(event.getMember()))
								.asBlocking()
								.tryConsume(adapter.getActionTokenPrice(), Duration.ofSeconds(10))) {
							adapter.handle(interactionHook, event).queue();
						} else
							interactionHook.editOriginal(R.Strings.ui("you_dont_have_enough_tokens_for_this_action_please_wait_a_few_seconds")).queue();
					} catch (Exception e) {
						new ExceptionHandler(e, event.getInteraction()).handle(interactionHook, event).queue();
						if (!BuildConfig.production) throw new RuntimeException(e);
					}
				});
	}

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final ButtonInteractionEvent event) throws BotException {
		if (config.isInMaintenance())
			return interactionHook.editOriginal((R.Strings.ui("the_bot_is_currently_in_maintenance_mode")));
		else return interactionHook.editOriginal(R.Strings.ui("you_do_not_have_the_permission_to_use_this_button"));
	}

	private @NonNull ButtonAdapter dispatch(@NonNull final ButtonInteractionEvent event) {
		return switch (event.getComponentId().split(":")) {
			case String[] b when b.length == 2 && b[0].equals("approve") && Permission.hasP2(config, event.getMember()) ->
					approveButton;
			case String[] b when b.length == 2 && b[0].equals("group") && Permission.hasP1() -> groupButton;
			case String[] b when b.length == 3 && b[0].equals("hns") && Permission.hasP1() -> hnsShowButton;
			case String[] b when b.length == 3 && b[0].equals("tutorial") && b[2].equals("suggestion") && Permission.hasP1() ->
					tutorialSuggestionButtons;
			case String[] b when b.length == 3 && b[0].equals("tutorial") && b[2].equals("list") && Permission.hasP1() ->
					tutorialListButtons;
			case String[] b when b.length == 3 && b[0].equals("list") && b[1].equals("pg") && Permission.hasP1() ->
					pgListButtons;
			case String[] b when b.length == 3 && b[0].equals("list") && b[1].equals("hns") && Permission.hasP1() ->
					hnsListButtons;
			case String[] b when b.length == 3 && b[0].equals("group") && Permission.hasP1() -> groupListButtons;
			case String[] b when b.length == 4 && b[0].equals("achievement") && Permission.hasP1() ->
					achievementListButtons;
			default -> this;
		};
	}

	@RequiredArgsConstructor
	class ExceptionHandler extends BaseComposeButton {
		@NonNull private final Exception exception;
		@NonNull private final ButtonInteraction interaction;

		@Override
		protected @NonNull MessageEditData compose(@NonNull final ButtonContext context) throws BotException {
			final var logChannel = requireChannel(context, config.getGuildId(), config.getLogChannelId());
			logChannel.sendMessageEmbeds(new ErrorEmbed(interaction, exception)).queue();
			return response("an_error_occurred_the_developer_has_been_notified");
		}
	}
}
