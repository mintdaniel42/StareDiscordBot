package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.commands.group.*;
import org.mintdaniel42.starediscordbot.commands.hns.HNSTutorialCommand;
import org.mintdaniel42.starediscordbot.commands.misc.ApproveChangeCommand;
import org.mintdaniel42.starediscordbot.commands.misc.InfoCommand;
import org.mintdaniel42.starediscordbot.commands.misc.MaintenanceCommand;
import org.mintdaniel42.starediscordbot.commands.user.UserDeleteCommand;
import org.mintdaniel42.starediscordbot.commands.user.UserEditCommand;
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
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
	@NonNull private final CommandAdapter hnsTutorialCommand;

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
		hnsTutorialCommand = new HNSTutorialCommand();
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
		if (Options.isInMaintenance())
			return interactionHook.editOriginal((R.Strings.ui("the_bot_is_currently_in_maintenance_mode")));
		else return interactionHook.editOriginal(R.Strings.ui("you_do_not_have_the_permission_to_use_this_command"));
	}

	@Contract("_ -> _")
	private @NonNull CommandAdapter handleCommand(@NonNull final SlashCommandInteractionEvent event) {
		// TODO make this a try / catch
		return switch (event.getFullCommandName()) {
			case String c when c.equals("approve") && Permissions.canEdit(event.getMember()) -> approveChangeCommand;
			case String c when c.equals("info") && Permissions.canView() -> infoCommand;
			case String c when c.equals("maintenance") && Permissions.canManage(event.getMember()) ->
					maintenanceCommand;
			case String c when c.equals("user delete") && Permissions.canCreate(event.getMember()) -> userDeleteCommand;
			case String c when c.equals("user edit") -> userEditCommand;
			case String c when c.equals("group show") && Permissions.canView() -> groupShowCommand;
			case String c when c.equals("group edit") -> groupEditCommand;
			case String c when c.equals("group create") && Permissions.canCreate(event.getMember()) ->
					groupCreateCommand;
			case String c when c.equals("group delete") && Permissions.canCreate(event.getMember()) ->
					groupDeleteCommand;
			case String c when c.equals("group user show") && Permissions.canView() -> groupUserShowCommand;
			case String c when c.equals("group user add") && Permissions.canEdit(event.getMember()) ->
					groupUserAddCommand;
			case String c when c.equals("group user remove") && Permissions.canEdit(event.getMember()) ->
					groupUserRemoveCommand;
			case String c when c.equals("hns tutorial") && Permissions.canView() -> hnsTutorialCommand;
			default -> this;
		};
	}
}
