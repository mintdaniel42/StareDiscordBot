package org.mintdaniel42.starediscordbot;

import com.coreoz.wisp.Scheduler;
import com.coreoz.wisp.schedule.Schedules;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.buttons.ButtonDispatcher;
import org.mintdaniel42.starediscordbot.commands.AutoCompletionHandler;
import org.mintdaniel42.starediscordbot.commands.CommandDispatcher;
import org.mintdaniel42.starediscordbot.commands.CommandList;
import org.mintdaniel42.starediscordbot.data.Database;
import org.mintdaniel42.starediscordbot.data.DatabaseConfig;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

import java.time.Duration;
import java.util.Arrays;

@Slf4j
public final class Bot extends ListenerAdapter {
	public Bot(@NonNull final Database database) {

		database.getHnsUserRepository().selectByPage(0);

		JDABuilder.createLight(Options.getToken())
				.addEventListeners(
						new AutoCompletionHandler(database),
						new CommandDispatcher(database),
						new ButtonDispatcher(database),
						this
				)
				.build();

		database.prepareDatabase();

		final var scheduler = new Scheduler();

		scheduler.schedule(
				database::cleanDatabase,
				Schedules.afterInitialDelay((timestamp, _, _) -> timestamp + BuildConfig.cleaningInterval - (timestamp % BuildConfig.cleaningInterval),
						Duration.ZERO)
		);
	}

	public static void main(@NonNull final String[] args) {
		//#if dev
		log.info(R.Strings.log("running_in_dev_mode"));
		//#endif
		new Bot(new Database(new DatabaseConfig()));
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
}
