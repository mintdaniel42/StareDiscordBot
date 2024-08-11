package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
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
import org.mintdaniel42.starediscordbot.commands.misc.ApproveChangeCommand;
import org.mintdaniel42.starediscordbot.commands.misc.InfoCommand;
import org.mintdaniel42.starediscordbot.commands.misc.MaintenanceCommand;
import org.mintdaniel42.starediscordbot.commands.pg.PGAddCommand;
import org.mintdaniel42.starediscordbot.commands.pg.PGEditCommand;
import org.mintdaniel42.starediscordbot.commands.pg.PGListCommand;
import org.mintdaniel42.starediscordbot.commands.pg.PGShowCommand;
import org.mintdaniel42.starediscordbot.commands.user.UserDeleteCommand;
import org.mintdaniel42.starediscordbot.commands.user.UserEditCommand;
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.embeds.ErrorEmbed;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.Permissions;
import org.mintdaniel42.starediscordbot.utils.R;

@Slf4j
public final class CommandDispatcher extends ListenerAdapter implements CommandAdapter {
	@NonNull private final CommandAdapter approveChangeCommand;
	@NonNull private final CommandAdapter infoCommand;
	@NonNull private final CommandAdapter maintenanceCommand;
	@NonNull private final CommandAdapter userDeleteCommand;
	@NonNull private final CommandAdapter userEditCommand;
	@NonNull private final CommandAdapter groupShowCommand;
	@NonNull private final CommandAdapter groupEditCommand;
	@NonNull private final CommandAdapter groupCreateCommand;
	@NonNull private final CommandAdapter groupDeleteCommand;
	@NonNull private final CommandAdapter groupUserShowCommand;
	@NonNull private final CommandAdapter groupUserAddCommand;
	@NonNull private final CommandAdapter groupUserRemoveCommand;
	@NonNull private final CommandAdapter hnsShowCommand;
	@NonNull private final CommandAdapter hnsShowMoreCommand;
	@NonNull private final CommandAdapter hnsAddCommand;
	@NonNull private final CommandAdapter hnsEditCommand;
	@NonNull private final CommandAdapter hnsListCommand;
	@NonNull private final CommandAdapter hnsTutorialCommand;
	@NonNull private final CommandAdapter pgShowCommand;
	@NonNull private final CommandAdapter pgAddCommand;
	@NonNull private final CommandAdapter pgEditCommand;
	@NonNull private final CommandAdapter pgListCommand;

	public CommandDispatcher(@NonNull final DatabaseAdapter databaseAdapter) {
		approveChangeCommand = new ApproveChangeCommand(databaseAdapter);
		infoCommand = new InfoCommand(databaseAdapter);
		maintenanceCommand = new MaintenanceCommand();
		userDeleteCommand = new UserDeleteCommand(databaseAdapter);
		userEditCommand = new UserEditCommand(databaseAdapter);
		groupShowCommand = new GroupShowCommand(databaseAdapter);
		groupEditCommand = new GroupEditCommand(databaseAdapter);
		groupCreateCommand = new GroupCreateCommand(databaseAdapter);
		groupDeleteCommand = new GroupDeleteCommand(databaseAdapter);
		groupUserShowCommand = new GroupUserShowCommand(databaseAdapter);
		groupUserAddCommand = new GroupUserAddCommand(databaseAdapter);
		groupUserRemoveCommand = new GroupUserRemoveCommand(databaseAdapter);
		hnsShowCommand = new HNSShowCommand(databaseAdapter);
		hnsShowMoreCommand = new HNSShowMoreCommand(databaseAdapter);
		hnsAddCommand = new HNSAddCommand(databaseAdapter);
		hnsEditCommand = new HNSEditCommand(databaseAdapter);
		hnsListCommand = new HNSListCommand(databaseAdapter);
		hnsTutorialCommand = new HNSTutorialCommand();
		pgShowCommand = new PGShowCommand(databaseAdapter);
		pgAddCommand = new PGAddCommand(databaseAdapter);
		pgEditCommand = new PGEditCommand(databaseAdapter);
		pgListCommand = new PGListCommand(databaseAdapter);
	}

	@Override
	public void onSlashCommandInteraction(@NonNull final SlashCommandInteractionEvent event) {
		//#if dev
		if (event.getMember() instanceof Member member) {
			log.info(R.Strings.log("command_s_invoked_by_user_s",
					event.getFullCommandName(),
					member.getEffectiveName()));
		}
		//#endif

		event.deferReply().queue(interactionHook -> handleCommand(event)
				.handle(interactionHook, event)
				.queue());
	}

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		return interactionHook.editOriginal((R.Strings.ui(Options.isInMaintenance() ? "the_bot_is_currently_in_maintenance_mode" : "you_do_not_have_the_permission_to_use_this_command")));
	}

	@Contract("_ -> _")
	private @NonNull CommandAdapter handleCommand(@NonNull final SlashCommandInteractionEvent event) {
		try {
			final var member = event.getMember();
			return switch (event.getFullCommandName()) {
				case String c when c.equals("approve") && Permissions.edit(member) -> approveChangeCommand;
				case String c when c.equals("info") && Permissions.view() -> infoCommand;
				case String c when c.equals("maintenance") && Permissions.manage(member) -> maintenanceCommand;
				case String c when c.equals("user delete") && Permissions.create(member) -> userDeleteCommand;
				case String c when c.equals("user edit") && Permissions.view() -> userEditCommand;
				case String c when c.equals("group show") && Permissions.view() -> groupShowCommand;
				case String c when c.equals("group edit") && Permissions.view() -> groupEditCommand;
				case String c when c.equals("group create") && Permissions.create(member) -> groupCreateCommand;
				case String c when c.equals("group delete") && Permissions.create(member) -> groupDeleteCommand;
				case String c when c.equals("group user show") && Permissions.view() -> groupUserShowCommand;
				case String c when c.equals("group user add") && Permissions.edit(member) -> groupUserAddCommand;
				case String c when c.equals("group user remove") && Permissions.edit(member) -> groupUserRemoveCommand;
				case String c when c.equals("hns show") && Permissions.view() -> hnsShowCommand;
				case String c when c.equals("hns showmore") && Permissions.view() -> hnsShowMoreCommand;
				case String c when c.equals("hns add") && Permissions.create(member) -> hnsAddCommand;
				case String c when c.equals("hns edit") && Permissions.view() -> hnsEditCommand;
				case String c when c.equals("hns list") && Permissions.view() -> hnsListCommand;
				case String c when c.equals("hns tutorial") && Permissions.view() -> hnsTutorialCommand;
				case String c when c.equals("pg show") && Permissions.view() -> pgShowCommand;
				case String c when c.equals("pg add") && Permissions.create(member) -> pgAddCommand;
				case String c when c.equals("pg edit") && Permissions.view() -> pgEditCommand;
				case String c when c.equals("pg list") && Permissions.view() -> pgListCommand;
				default -> this;
			};
		} catch (final Exception e) {
			return new ErrorHandler(e);
		}
	}

	private record ErrorHandler(@NonNull Exception exception) implements CommandAdapter {
		@Override
		public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
			if (event.getGuild() instanceof final Guild guild) {
				if (guild.getTextChannelById(Options.getLogChannelId()) instanceof final TextChannel channel) {
					channel.sendMessageEmbeds(ErrorEmbed.of(event.getInteraction(), exception)).queue();
				}
			}
			return interactionHook.editOriginal(R.Strings.ui("an_error_occured_the_developer_has_been_notified"));
		}
	}
}
