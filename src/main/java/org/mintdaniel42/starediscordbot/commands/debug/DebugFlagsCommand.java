package org.mintdaniel42.starediscordbot.commands.debug;

import io.avaje.config.Config;
import io.avaje.inject.RequiresBean;
import io.avaje.inject.RequiresProperty;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.mintdaniel42.starediscordbot.BotConfig;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.Comparator;

@RequiredArgsConstructor
@RequiresBean(DebugCommand.class)
@RequiresProperty(value = "feature.command.debug.flags.enabled", equalTo = "true")
@Singleton
public final class DebugFlagsCommand extends BaseComposeCommand {
	@NonNull private final BotConfig config;

	@Override
	protected @NonNull MessageEditData compose(@NonNull final CommandContext context) throws BotException {
		final var stringBuilder = new StringBuilder("```");
		for (final var entry : Config.asProperties().entrySet()
				.stream()
				.sorted(Comparator.comparing(o -> o.getKey().toString()))
				.toList()) {
			if (!entry.getValue().equals(config.getToken())) {
				stringBuilder.append(entry).append("\n");
			} else {
				stringBuilder.append(entry.getKey()).append("=CENSORED\n");
			}
		}
		return response(stringBuilder.append("```").toString());
	}

	@Inject
	public void register(@NonNull @Named("debug") SlashCommandData command) {
		command.addSubcommands(new SubcommandData("flags", R.Strings.ui("show_bot_flags")));
	}

	@Override
	public @NonNull String getCommandId() {
		return "debug flags";
	}
}
