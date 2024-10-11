package org.mintdaniel42.starediscordbot;

import com.coreoz.wisp.Scheduler;
import com.coreoz.wisp.schedule.Schedules;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.data.Database;
import org.mintdaniel42.starediscordbot.di.DI;
import org.mintdaniel42.starediscordbot.utils.R;

import java.time.Duration;

@RequiredArgsConstructor
@Singleton
@Slf4j
public final class Bot {
	@NonNull private final Database database;
	@NonNull private final Scheduler scheduler;
	@NonNull private final BotConfig config;

	public static void main() {
		if (!BuildConfig.production) log.info(R.Strings.log("running_in_dev_mode"));
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

		scheduler.schedule(
				database::cleanDatabase, Schedules.afterInitialDelay(
						Schedules.fixedFrequencySchedule(
								Duration.ofMillis(BuildConfig.cleaningInterval)
						),
						Duration.ZERO)
		);
	}

	private void onReady(@NonNull final GuildReadyEvent event) {
		// setup commands
		event.getGuild()
				.updateCommands()
				.addCommands(DI.list(SlashCommandData.class))
				.queue();
	}
}
