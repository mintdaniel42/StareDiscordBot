package org.mintdaniel42.starediscordbot;

import com.coreoz.wisp.Scheduler;
import com.coreoz.wisp.schedule.Schedules;
import io.avaje.inject.BeanScope;
import jakarta.inject.Singleton;
import lombok.Cleanup;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.commands.CommandList;
import org.mintdaniel42.starediscordbot.data.Database;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

import java.time.Duration;
import java.util.Arrays;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Singleton
@Slf4j
public final class Bot implements Consumer<GuildReadyEvent> {
	@NonNull private final Database database;
	@NonNull private final Scheduler scheduler;

	@SuppressWarnings("unused")
	public static void main(@NonNull final String[] args) {
		//#if dev
		log.info(R.Strings.log("running_in_dev_mode"));
		//#endif
		@Cleanup final var beanScope = BeanScope.builder().build();
		beanScope.get(Bot.class).run();
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public void run() {
		@Cleanup final var beanScope = BeanScope.builder().build();
		JDABuilder.createDefault(Options.getToken())
				.addEventListeners(beanScope.list(ListenerAdapter.class).toArray())
				.build()
				.listenOnce(GuildReadyEvent.class)
				.filter(e -> e.getGuild().getIdLong() == Options.getGuildId())
				.subscribe(this);

		database.prepareDatabase();

		scheduler.schedule(
				database::cleanDatabase, Schedules.afterInitialDelay(
						Schedules.fixedFrequencySchedule(
								Duration.ofMillis(BuildConfig.cleaningInterval)
						),
						Duration.ZERO)
		);
	}

	@Override
	public void accept(@NonNull final GuildReadyEvent event) {
		// setup commands
		event.getGuild()
				.updateCommands()
				.addCommands(Arrays.stream(CommandList.values())
						.map(CommandList::get)
						.toArray(CommandData[]::new))
				.queue();
	}
}
