package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.BotConfig;
import org.mintdaniel42.starediscordbot.bucket.RateLimited;

/**
 * Implementing this interface is equivalent to implementing the logic for a command
 */
public interface CommandAdapter extends RateLimited {
	/**
	 * @param event           contains read-only data like {@link OptionMapping}, should not be used for responding
	 * @return {@link MessageEditData} the response to be sent
	 */
	@NonNull
	MessageEditData handle(@NonNull final SlashCommandInteractionEvent event);

	@NonNull
	String getCommandId();

	@Contract("_, null -> false")
	default boolean hasPermission(@NonNull final BotConfig config, @Nullable final Member member) {
		return member != null;
	}
}
