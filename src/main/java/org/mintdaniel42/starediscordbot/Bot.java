package org.mintdaniel42.starediscordbot;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.mintdaniel42.starediscordbot.buttons.ApproveChangeButton;
import org.mintdaniel42.starediscordbot.buttons.ListButtons;
import org.mintdaniel42.starediscordbot.commands.*;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.utils.CommandEngine;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

@Slf4j
public final class Bot extends ListenerAdapter {
	@NonNull private final DatabaseAdapter databaseAdapter;

	public Bot(@NonNull final DatabaseAdapter databaseAdapter) {
		this.databaseAdapter = databaseAdapter;

		JDABuilder.createLight(Options.getToken())
				.addEventListeners(
						new AutoCompletionHandler(databaseAdapter),

						new ListButtons(databaseAdapter),
						new ApproveChangeButton(databaseAdapter),

						new MaintenanceCommand(),
						new HelpCommand(),

						new UserCommand(databaseAdapter),
						new HNSCommand(databaseAdapter),
						new PGCommand(databaseAdapter),
						new GroupCommand(databaseAdapter),
						new ApproveChangeCommand(databaseAdapter),

						this
				)
				.build();
	}

	public static void main(@NonNull final String[] args) throws Exception {
		//#if dev
		log.info(R.Strings.log("running_in_dev_mode"));
		//#endif
		new Bot(new DatabaseAdapter(Options.getJdbcUrl()));
	}

	@Override
	public void onGuildReady(@NonNull final GuildReadyEvent event) {
		// check if correct guild
		if (event.getGuild().getIdLong() == Options.getGuildId()) {
			// setup commands
			event.getGuild()
					.updateCommands()
					.addCommands(CommandEngine.generateCommands("commands.json"))
					.queue();
		}
	}

	@Override
	public void onShutdown(@NonNull final ShutdownEvent event) {
		try {
			databaseAdapter.close();
		} catch (Exception e) {
			log.error(R.Strings.log("could_not_close_database"), e);
			throw new RuntimeException(e);
		}
	}

	//#if dev
	@Override
	public void onSlashCommandInteraction(@NonNull final SlashCommandInteractionEvent event) {
		if (event.getMember() instanceof Member member) {
			log.info(R.Strings.log("command_s_invoked_by_user_s",
					event.getFullCommandName(),
					member.getEffectiveName()));
		}
	}

	@Override
	public void onButtonInteraction(@NonNull final ButtonInteractionEvent event) {
		if (event.getMember() instanceof Member member) {
			log.info(R.Strings.log("button_s_pressed_by_user_s",
					event.getComponentId(),
					member.getEffectiveName()));
		}
	}
	//#endif
}
