package org.mintdaniel42.starediscordbot.commands;

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

@RequiredArgsConstructor
@Slf4j
public final class CommandDispatcher extends ListenerAdapter implements CommandAdapter {
	@NonNull private final DatabaseAdapter databaseAdapter;

	@Override
	public void onSlashCommandInteraction(@NonNull final SlashCommandInteractionEvent event) {
		//#if dev
		if (event.getMember() instanceof Member member) {
			log.info(R.Strings.log("command_s_invoked_by_user_s",
					event.getFullCommandName(),
					member.getEffectiveName()));
		}
		//#endif

		event.deferReply().queue(interactionHook -> {
			try {
				handleCommand(event)
						.handle(interactionHook, event)
						.queue();
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
	private @NonNull CommandAdapter handleCommand(@NonNull final SlashCommandInteractionEvent event) {
		return switch (event.getFullCommandName()) {
			case String c when c.equals("approve") && Permissions.edit(event.getMember()) ->
					new ApproveChangeCommand(databaseAdapter);
			case String c when c.equals("info") && Permissions.view() -> new InfoCommand(databaseAdapter);
			case String c when c.equals("maintenance") && Permissions.manage(event.getMember()) ->
					new MaintenanceCommand();
			case String c when c.equals("user delete") && Permissions.create(event.getMember()) ->
					new UserDeleteCommand(databaseAdapter);
			case String c when c.equals("user edit") && Permissions.view() -> new UserEditCommand(databaseAdapter);
			case String c when c.equals("group show") && Permissions.view() -> new GroupShowCommand(databaseAdapter);
			case String c when c.equals("group edit") && Permissions.view() -> new GroupEditCommand(databaseAdapter);
			case String c when c.equals("group create") && Permissions.create(event.getMember()) ->
					new GroupCreateCommand(databaseAdapter);
			case String c when c.equals("group delete") && Permissions.create(event.getMember()) ->
					new GroupDeleteCommand(databaseAdapter);
			case String c when c.equals("group user show") && Permissions.view() ->
					new GroupUserShowCommand(databaseAdapter);
			case String c when c.equals("group user add") && Permissions.edit(event.getMember()) ->
					new GroupUserAddCommand(databaseAdapter);
			case String c when c.equals("group user remove") && Permissions.edit(event.getMember()) ->
					new GroupUserRemoveCommand(databaseAdapter);
			case String c when c.equals("hns show") && Permissions.view() -> new HNSShowCommand(databaseAdapter);
			case String c when c.equals("hns showmore") && Permissions.view() ->
					new HNSShowMoreCommand(databaseAdapter);
			case String c when c.equals("hns add") && Permissions.create(event.getMember()) ->
					new HNSAddCommand(databaseAdapter);
			case String c when c.equals("hns edit") && Permissions.view() -> new HNSEditCommand(databaseAdapter);
			case String c when c.equals("hns list") && Permissions.view() -> new HNSListCommand(databaseAdapter);
			case String c when c.equals("hns tutorial") && Permissions.view() -> new HNSTutorialCommand();
			case String c when c.equals("hns achievements add") && Permissions.view() ->
					new AchievementsAddCommand(databaseAdapter);
			case String c when c.equals("hns achievements list") && Permissions.view() ->
					new AchievementsListCommand(databaseAdapter);
			case String c when c.equals("pg show") && Permissions.view() -> new PGShowCommand(databaseAdapter);
			case String c when c.equals("pg add") && Permissions.create(event.getMember()) ->
					new PGAddCommand(databaseAdapter);
			case String c when c.equals("pg edit") && Permissions.view() -> new PGEditCommand(databaseAdapter);
			case String c when c.equals("pg list") && Permissions.view() -> new PGListCommand(databaseAdapter);
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
