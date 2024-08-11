package org.mintdaniel42.starediscordbot;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.mintdaniel42.starediscordbot.buttons.ButtonDispatcher;
import org.mintdaniel42.starediscordbot.buttons.ListButtons;
import org.mintdaniel42.starediscordbot.buttons.TutorialButtons;
import org.mintdaniel42.starediscordbot.commands.*;
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.Arrays;

@Slf4j
public final class Bot extends ListenerAdapter {
	@NonNull private final DatabaseAdapter databaseAdapter;

	public Bot(@NonNull final DatabaseAdapter databaseAdapter) {
		this.databaseAdapter = databaseAdapter;

		JDABuilder.createLight(Options.getToken())
				.addEventListeners(
						new AutoCompletionHandler(databaseAdapter),

						new ListButtons(databaseAdapter),
						new TutorialButtons(),

						new HelpCommand(),

						new HNSCommand(databaseAdapter),
						new PGCommand(databaseAdapter),

						new CommandDispatcher(databaseAdapter),
						new ButtonDispatcher(databaseAdapter),

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
					.addCommands(Arrays.stream(CommandList.values())
							.map(CommandList::get)
							.toArray(CommandData[]::new))
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
}
