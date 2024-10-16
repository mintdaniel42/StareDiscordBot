package org.mintdaniel42.starediscordbot.commands;

import com.codahale.metrics.MetricRegistry;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.BotConfig;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.commands.misc.MaintenanceCommand;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.embeds.ErrorEmbed;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.R;

import java.time.Duration;
import java.util.List;

@RequiredArgsConstructor
@Singleton
@Slf4j
public final class CommandDispatcher extends ListenerAdapter {
	@NonNull private final List<CommandAdapter> commandAdapters;
	@NonNull private final MetricRegistry metricRegistry;
	@NonNull private final BotConfig config;

	@Override
	public void onSlashCommandInteraction(@NonNull final SlashCommandInteractionEvent event) {
		if (!BuildConfig.production && event.getMember() instanceof final Member member) {
			log.info(R.Strings.log("command_s_invoked_by_user_s",
					event.getFullCommandName(),
					member.getEffectiveName()));
		}

		final var adapter = dispatch(event);
		event.deferReply()
				.setEphemeral(adapter.isPublicResponseRestricted())
				.queue(interactionHook -> respond(interactionHook, adapter, event));
	}

	@Contract("_ -> _")
	private @NonNull CommandAdapter dispatch(@NonNull final SlashCommandInteractionEvent event) {
		return commandAdapters.stream()
				.filter(commandAdapter -> commandAdapter.getCommandId().equals(event.getFullCommandName()))
				.findFirst()
				.orElseGet(CommandNotFoundHandler::new);
	}

	private void respond(@NonNull final InteractionHook interactionHook, @NonNull final CommandAdapter adapter, @NonNull final SlashCommandInteractionEvent event) {
		try (final var _ = metricRegistry.timer(adapter.getCommandId()).time()) {
			if (!adapter.hasPermission(config, event.getMember())) {
				interactionHook.editOriginal(R.Strings.ui("you_do_not_have_the_permission_to_use_this_command"))
						.queue();
			} else if (config.isInMaintenance() && !(adapter instanceof MaintenanceCommand)) {
				interactionHook.editOriginal(R.Strings.ui("the_bot_is_currently_in_maintenance_mode"))
						.queue();
			} else if (adapter instanceof MaintenanceCommand) {
				interactionHook.editOriginal(adapter.handle(event)).queue();
			} else if (adapter.getPool()
					.getBucket(event.getMember())
					.asBlocking()
					.tryConsume(adapter.getActionTokenPrice(), Duration.ofSeconds(10))) {
				interactionHook.editOriginal(adapter.handle(event))
						.queue();
			} else
				interactionHook.editOriginal(R.Strings.ui("you_dont_have_enough_tokens_for_this_action_please_wait_a_few_seconds"))
						.queue();
		} catch (Exception e) {
			interactionHook.editOriginal(new ExceptionHandler(e, event.getInteraction()).handle(event)).queue();
			if (!BuildConfig.production) throw new RuntimeException(e);
		}
	}

	@RequiredArgsConstructor
	static class CommandNotFoundHandler extends BaseComposeCommand {
		@Override
		protected @NonNull MessageEditData compose(@NonNull final CommandContext context) throws BotException {
			return response("this_command_is_currently_unavailable");
		}

		@Override
		public @NonNull String getCommandId() {
			return "";
		}
	}

	@RequiredArgsConstructor
	class ExceptionHandler extends BaseComposeCommand {
		@NonNull private final Exception exception;
		@NonNull private final SlashCommandInteraction interaction;

		@Override
		protected @NonNull MessageEditData compose(@NonNull final CommandContext context) throws BotException {
			final var logChannel = requireChannel(context, config.getGuildId(), config.getLogChannelId());
			logChannel.sendMessageEmbeds(new ErrorEmbed(interaction, exception)).queue();
			return response("an_error_occurred_the_developer_has_been_notified");
		}

		@Override
		public @NonNull String getCommandId() {
			return "";
		}
	}
}
