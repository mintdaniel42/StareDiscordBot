package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.utils.DCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

public final class CommandDispatcher extends ListenerAdapter implements CommandAdapter {
	@NonNull private final CommandAdapter approveChangeCommand, infoCommand, maintenanceCommand;

	public CommandDispatcher(@NonNull final DatabaseAdapter databaseAdapter) {
		approveChangeCommand = new ApproveChangeCommand(databaseAdapter);
		infoCommand = new InfoCommand(databaseAdapter);
		maintenanceCommand = new MaintenanceCommand();
	}

	@Override
	public void onSlashCommandInteraction(@NonNull final SlashCommandInteractionEvent event) {
		event.deferReply().queue(interactionHook -> handleCommand(event)
				.handle(interactionHook, event)
				.queue());
	}

	@Override
	public @NonNull RestAction<Message> handle(@NonNull InteractionHook interactionHook, @NonNull SlashCommandInteractionEvent event) {
		if (Options.isInMaintenance())
			return interactionHook.editOriginal((R.Strings.ui("the_bot_is_currently_in_maintenance_mode")));
		else return interactionHook.editOriginal(R.Strings.ui("you_do_not_have_the_permission_to_use_this_command"));
	}

	private @NonNull CommandAdapter handleCommand(@NonNull final SlashCommandInteractionEvent event) {
		// TODO make this a try / catch
		return switch (event.getFullCommandName()) {
			case String c when c.equals("approve") && canEdit(event.getMember()) -> approveChangeCommand;
			case String c when c.equals("info") && canView() -> infoCommand;
			case String c when c.equals("maintenance") && canManage(event.getMember()) -> maintenanceCommand;
			default -> this;
		};
	}

	@Contract(pure = true)
	private boolean canView() {
		return !Options.isInMaintenance();
	}

	@Contract("null -> false")
	private boolean canEdit(@Nullable final Member member) {
		if (Options.isInMaintenance()) return false;
		return DCHelper.hasRole(member, Options.getEditRoleId()) || DCHelper.hasRole(member, Options.getCreateRoleId());
	}

	@Contract("null -> false")
	private boolean canCreate(@Nullable final Member member) {
		if (Options.isInMaintenance()) return false;
		return DCHelper.hasRole(member, Options.getCreateRoleId());
	}

	@Contract("null -> false")
	private boolean canManage(@Nullable final Member member) {
		if (member == null) return false;
		return member.hasPermission(Permission.ADMINISTRATOR);
	}
}
