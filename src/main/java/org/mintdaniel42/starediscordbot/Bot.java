package org.mintdaniel42.starediscordbot;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.mintdaniel42.starediscordbot.buttons.ApproveChangeButton;
import org.mintdaniel42.starediscordbot.buttons.ListButtons;
import org.mintdaniel42.starediscordbot.commands.*;
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public final class Bot extends ListenerAdapter {
	@NonNull private final JDA jda;
	@NonNull private final DatabaseAdapter databaseAdapter;

	public Bot(@NonNull final DatabaseAdapter databaseAdapter) {
		this.databaseAdapter = databaseAdapter;

		jda = JDABuilder.createLight(Options.getToken())
				.addEventListeners(
						new AutoCompletionHandler(databaseAdapter),

						new ListButtons(databaseAdapter),
						new ApproveChangeButton(databaseAdapter),

						new MaintenanceCommand(),
						new HelpCommand(),
						new InfoCommand(databaseAdapter),

						new TutorialCommand(),

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
					.addCommands(Arrays.stream(CommandList.values())
							.map(CommandList::get)
							.toArray(CommandData[]::new))
					.queue();

			// start MOTD thread
			new Thread(this::changeMotd).start();
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

	private void changeMotd() {
		if (!Options.isInMaintenance()) {
			try {
				final var url = new URL("https://raw.githubusercontent.com/InventivetalentDev/minecraft-assets/1.21/assets/minecraft/texts/splashes.txt");
				final var connection = url.openConnection();
				final var inputStream = connection.getInputStream();

				final var scanner = new Scanner(inputStream).useDelimiter("\\A");
				final var lines = (scanner.hasNext() ? scanner.next() : "").split("\\n");
				final var random = new Random();

				final var onlineStatus = OnlineStatus.ONLINE;
				final var activity = Activity.customStatus(lines[random.nextInt(lines.length)]);

				jda.getPresence().setPresence(onlineStatus, activity);
			} catch (IOException _) {
			}
		}

		float next = TimeUnit.HOURS.toMillis(1) - System.currentTimeMillis() % TimeUnit.HOURS.toMillis(1);
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				changeMotd();
			}
		}, Math.round(next));
	}
}
