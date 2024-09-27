package org.mintdaniel42.starediscordbot;

import com.codahale.metrics.MetricRegistry;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.mintdaniel42.starediscordbot.commands.CommandList;
import org.mintdaniel42.starediscordbot.data.Database;
import org.mintdaniel42.starediscordbot.di.DI;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.Arrays;

@RequiredArgsConstructor
@Singleton
@Slf4j
public final class Bot {
	@NonNull private final Database database;
	//@NonNull private final Scheduler scheduler;
	@NonNull private final BotConfig config;
	@NonNull private final MetricRegistry metrics;

	public static void main(String[] args) {
		//#if dev
		log.info(R.Strings.log("running_in_dev_mode"));
		//#endif
		DI.get(Bot.class).run();
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public void run() {
		final var jda = JDABuilder.createDefault(config.getToken())
				.addEventListeners(DI.list(ListenerAdapter.class).toArray())
				.build();

		jda.listenOnce(GuildReadyEvent.class)
				.filter(e -> e.getGuild().getIdLong() == config.getGuildId())
				.subscribe(this::onReady);

		database.prepareDatabase();

		/*scheduler.schedule(
				database::cleanDatabase, Schedules.afterInitialDelay(
						Schedules.fixedFrequencySchedule(
								Duration.ofMillis(BuildConfig.cleaningInterval)
						),
						Duration.ZERO)
		);*/
	}

	private void onReady(@NonNull final GuildReadyEvent event) {
		// setup commands
		event.getGuild()
				.updateCommands()
				.addCommands(Arrays.stream(CommandList.values())
						.map(CommandList::get)
						.toArray(CommandData[]::new))
				.queue();
	}
}
