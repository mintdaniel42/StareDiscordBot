package org.mintdaniel42.starediscordbot.commands;

import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.commands.group.*;
import org.mintdaniel42.starediscordbot.commands.hns.*;
import org.mintdaniel42.starediscordbot.commands.hns.achievements.AchievementsAddCommand;
import org.mintdaniel42.starediscordbot.commands.hns.achievements.AchievementsListCommand;
import org.mintdaniel42.starediscordbot.commands.hns.maps.MapsAddCommand;
import org.mintdaniel42.starediscordbot.commands.misc.ApproveChangeCommand;
import org.mintdaniel42.starediscordbot.commands.misc.InfoCommand;
import org.mintdaniel42.starediscordbot.commands.misc.MaintenanceCommand;
import org.mintdaniel42.starediscordbot.commands.pg.PGAddCommand;
import org.mintdaniel42.starediscordbot.commands.pg.PGEditCommand;
import org.mintdaniel42.starediscordbot.commands.pg.PGListCommand;
import org.mintdaniel42.starediscordbot.commands.pg.PGShowCommand;
import org.mintdaniel42.starediscordbot.commands.user.UserDeleteCommand;
import org.mintdaniel42.starediscordbot.commands.user.UserEditCommand;
import org.mintdaniel42.starediscordbot.embeds.ErrorEmbed;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.Permission;
import org.mintdaniel42.starediscordbot.utils.R;

import java.time.Duration;
import java.util.Objects;

@RequiredArgsConstructor
@Singleton
@Slf4j
public final class CommandDispatcher extends ListenerAdapter implements CommandAdapter {
	@NonNull private final ApproveChangeCommand approveChangeCommand;
	@NonNull private final InfoCommand infoCommand;
	@NonNull private final MaintenanceCommand maintenanceCommand;
	@NonNull private final UserDeleteCommand userDeleteCommand;
	@NonNull private final UserEditCommand userEditCommand;
	@NonNull private final GroupShowCommand groupShowCommand;
	@NonNull private final GroupEditCommand groupEditCommand;
	@NonNull private final GroupCreateCommand groupCreateCommand;
	@NonNull private final GroupDeleteCommand groupDeleteCommand;
	@NonNull private final GroupUserShowCommand groupUserShowCommand;
	@NonNull private final GroupUserAddCommand groupUserAddCommand;
	@NonNull private final GroupUserRemoveCommand groupUserRemoveCommand;
	@NonNull private final HNSShowCommand hnsShowCommand;
	@NonNull private final HNSShowMoreCommand hnsShowMoreCommand;
	@NonNull private final HNSAddCommand hnsAddCommand;
	@NonNull private final HNSEditCommand hnsEditCommand;
	@NonNull private final HNSListCommand hnsListCommand;
	@NonNull private final HNSTutorialCommand hnsTutorialCommand;
	@NonNull private final AchievementsAddCommand achievementsAddCommand;
	@NonNull private final AchievementsListCommand achievementsListCommand;
	@NonNull private final MapsAddCommand mapsAddCommand;
	@NonNull private final PGShowCommand pgShowCommand;
	@NonNull private final PGAddCommand pgAddCommand;
	@NonNull private final PGEditCommand pgEditCommand;
	@NonNull private final PGListCommand pgListCommand;

	@Override
	public void onSlashCommandInteraction(@NonNull final SlashCommandInteractionEvent event) {
		//#if dev
		if (event.getMember() instanceof Member member) {
			log.info(R.Strings.log("command_s_invoked_by_user_s",
					event.getFullCommandName(),
					member.getEffectiveName()));
		}
		//#endif

		final var adapter = dispatch(event);
		event.deferReply()
				.setEphemeral(adapter.isPublicResponseRestricted())
				.queue(interactionHook -> {
					try {
						if (adapter instanceof MaintenanceCommand || adapter.getPool()
								.getBucket(Objects.requireNonNull(event.getMember()))
								.asBlocking()
								.tryConsume(adapter.getActionTokenPrice(), Duration.ofSeconds(10))) {
							adapter.handle(interactionHook, event).queue();
				} else
					interactionHook.editOriginal(R.Strings.ui("you_dont_have_enough_tokens_for_this_action_please_wait_a_few_seconds")).queue();
					} catch (Exception e) {
						new ErrorHandler(e).handle(interactionHook, event).queue();
					}
				});
	}

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		return interactionHook.editOriginal((R.Strings.ui(Options.isInMaintenance() ? "the_bot_is_currently_in_maintenance_mode" : "you_do_not_have_the_permission_to_use_this_command")));
	}

	@Contract("_ -> _")
	private @NonNull CommandAdapter dispatch(@NonNull final SlashCommandInteractionEvent event) {
		return switch (event.getFullCommandName()) {
			case String c when c.equals("approve") && Permission.hasP2(event.getMember()) -> approveChangeCommand;
			case String c when c.equals("info") && Permission.hasP1() -> infoCommand;
			case String c when c.equals("maintenance") && Permission.hasAdmin(event.getMember()) -> maintenanceCommand;
			case String c when c.equals("user delete") && Permission.hasP4(event.getMember()) -> userDeleteCommand;
			case String c when c.equals("user edit") && Permission.hasP1() -> userEditCommand;
			case String c when c.equals("group show") && Permission.hasP1() -> groupShowCommand;
			case String c when c.equals("group edit") && Permission.hasP1() -> groupEditCommand;
			case String c when c.equals("group create") && Permission.hasP4(event.getMember()) -> groupCreateCommand;
			case String c when c.equals("group delete") && Permission.hasP4(event.getMember()) -> groupDeleteCommand;
			case String c when c.equals("group user show") && Permission.hasP1() -> groupUserShowCommand;
			case String c when c.equals("group user add") && Permission.hasP2(event.getMember()) -> groupUserAddCommand;
			case String c when c.equals("group user remove") && Permission.hasP2(event.getMember()) ->
					groupUserRemoveCommand;
			case String c when c.equals("hns show") && Permission.hasP1() -> hnsShowCommand;
			case String c when c.equals("hns showmore") && Permission.hasP1() -> hnsShowMoreCommand;
			case String c when c.equals("hns add") && Permission.hasP4(event.getMember()) -> hnsAddCommand;
			case String c when c.equals("hns edit") && Permission.hasP1() -> hnsEditCommand;
			case String c when c.equals("hns list") && Permission.hasP1() -> hnsListCommand;
			case String c when c.equals("hns tutorial") && Permission.hasP1() -> hnsTutorialCommand;
			case String c when c.equals("hns achievements add") && Permission.hasP1() -> achievementsAddCommand;
			case String c when c.equals("hns achievements list") && Permission.hasP1() -> achievementsListCommand;
			case String c when c.equals("hns maps add") && Permission.hasP4(event.getMember()) -> mapsAddCommand;
			case String c when c.equals("pg show") && Permission.hasP1() -> pgShowCommand;
			case String c when c.equals("pg add") && Permission.hasP4(event.getMember()) -> pgAddCommand;
			case String c when c.equals("pg edit") && Permission.hasP1() -> pgEditCommand;
			case String c when c.equals("pg list") && Permission.hasP1() -> pgListCommand;
			default -> this;
		};
	}

	private record ErrorHandler(@NonNull Exception exception) implements CommandAdapter {
		@Override
		public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
			if (event.getGuild() instanceof final Guild guild) {
				if (guild.getTextChannelById(Options.getLogChannelId()) instanceof final TextChannel channel) {
					channel.sendMessageEmbeds(ErrorEmbed.of(event.getInteraction(), exception)).queue();
				}
			}
			return interactionHook.editOriginal(R.Strings.ui("an_error_occurred_the_developer_has_been_notified"));
		}
	}
}
