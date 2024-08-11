package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.commands.group.GroupShowCommand;
import org.mintdaniel42.starediscordbot.commands.user.UserDeleteCommand;
import org.mintdaniel42.starediscordbot.commands.user.UserEditCommand;
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.utils.DCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

public final class CommandDispatcher extends ListenerAdapter implements CommandAdapter {
	@NonNull private final CommandAdapter approveChangeCommand;
	@NonNull private final CommandAdapter infoCommand;
	@NonNull private final CommandAdapter maintenanceCommand;
	@NonNull private final CommandAdapter userDeleteCommand;
	@NonNull private final CommandAdapter userEditCommand;
	@NonNull private final CommandAdapter groupShowCommand;

	public CommandDispatcher(@NonNull final DatabaseAdapter databaseAdapter) {
		approveChangeCommand = new ApproveChangeCommand(databaseAdapter);
		infoCommand = new InfoCommand(databaseAdapter);
		maintenanceCommand = new MaintenanceCommand();
		userDeleteCommand = new UserDeleteCommand(databaseAdapter);
		userEditCommand = new UserEditCommand(databaseAdapter);
		groupShowCommand = new GroupShowCommand(databaseAdapter);
	}

	@Override
	public void onSlashCommandInteraction(@NonNull final SlashCommandInteractionEvent event) {
		event.deferReply().queue(interactionHook -> handleCommand(event)
				.handle(interactionHook, event)
				.queue());
	}

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull InteractionHook interactionHook, @NonNull SlashCommandInteractionEvent event) {
		if (Options.isInMaintenance())
			return interactionHook.editOriginal((R.Strings.ui("the_bot_is_currently_in_maintenance_mode")));
		else return interactionHook.editOriginal(R.Strings.ui("you_do_not_have_the_permission_to_use_this_command"));
	}

	@Contract(pure = true)
	public static boolean canView() {
		return !Options.isInMaintenance();
	}

	@Contract("null -> false")
	public static boolean canEdit(@Nullable final Member member) {
		if (Options.isInMaintenance()) return false;
		return DCHelper.hasRole(member, Options.getEditRoleId()) || DCHelper.hasRole(member, Options.getCreateRoleId());
	}

	@Contract("null -> false")
	public static boolean canCreate(@Nullable final Member member) {
		if (Options.isInMaintenance()) return false;
		return DCHelper.hasRole(member, Options.getCreateRoleId());
	}

	@Contract("null -> false")
	public static boolean canManage(@Nullable final Member member) {
		if (member == null) return false;
		return member.hasPermission(Permission.ADMINISTRATOR);
	}

	@Contract("_ -> _")
	private @NonNull CommandAdapter handleCommand(@NonNull final SlashCommandInteractionEvent event) {
		// TODO make this a try / catch
		return switch (event.getFullCommandName()) {
			case String c when c.equals("approve") && canEdit(event.getMember()) -> approveChangeCommand;
			case String c when c.equals("info") && canView() -> infoCommand;
			case String c when c.equals("maintenance") && canManage(event.getMember()) -> maintenanceCommand;
			case String c when c.equals("user delete") && canCreate(event.getMember()) -> userDeleteCommand;
			case String c when c.equals("user edit") -> userEditCommand;
			case String c when c.equals("group show") -> groupShowCommand;
			default -> this;
		};
	}
}
