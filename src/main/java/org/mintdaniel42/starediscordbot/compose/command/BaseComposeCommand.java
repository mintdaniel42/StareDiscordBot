package org.mintdaniel42.starediscordbot.compose.command;

import com.codahale.metrics.MetricRegistry;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.mintdaniel42.starediscordbot.commands.CommandAdapter;
import org.mintdaniel42.starediscordbot.compose.Composer;
import org.mintdaniel42.starediscordbot.compose.exception.CommandIncompleteException;
import org.mintdaniel42.starediscordbot.exception.BotException;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class BaseComposeCommand extends Composer<CommandContext> implements CommandAdapter {
	@NonNull @Setter(onMethod_ = @Inject) private MetricRegistry metrics;

	/* ========== OPTIONS META ========== */
	protected static void requireOptionCount(@NonNull final CommandContext context, final int atLeast) throws CommandIncompleteException {
		if (context.getOptionCount() < atLeast) {
			throw new CommandIncompleteException();
		}
	}

	protected static @NonNull <T> T requireOptionCount(@NonNull final CommandContext context, final int atLeast, @NonNull final Supplier<T> supplier) throws CommandIncompleteException {
		if (context.getOptionCount() < atLeast) {
			throw new CommandIncompleteException();
		}
		return supplier.get();
	}

	/* ========== ATTACHMENT OPTIONS ========== */
	protected static @NonNull Optional<Message.Attachment> nullableAttachmentOption(@NonNull final CommandContext context, @NonNull final String key) {
		return context.getOption(key)
				.map(OptionMapping::getAsAttachment);
	}

	protected static @NonNull <T> Optional<T> nullableAttachmentOption(@NonNull final CommandContext context, @NonNull final String key, @NonNull final Function<Message.Attachment, T> function) {
		return context.getOption(key)
				.map(OptionMapping::getAsAttachment)
				.map(function);
	}

	protected static @NonNull Message.Attachment requireAttachmentOption(@NonNull final CommandContext context, @NonNull final String key) throws CommandIncompleteException {
		return nullableAttachmentOption(context, key)
				.orElseThrow(CommandIncompleteException::new);
	}

	protected static @NonNull <T> T requireAttachmentOption(@NonNull final CommandContext context, @NonNull final String key, @NonNull final Function<Message.Attachment, T> function) throws CommandIncompleteException {
		return nullableAttachmentOption(context, key, function)
				.orElseThrow(CommandIncompleteException::new);
	}

	/* ========== STRING OPTIONS ========== */
	protected static @NonNull Optional<String> nullableStringOption(@NonNull final CommandContext context, @NonNull final String key) {
		return context.getOption(key)
				.map(OptionMapping::getAsString);
	}

	protected static @NonNull <T> Optional<T> nullableStringOption(@NonNull final CommandContext context, @NonNull final String key, @NonNull final Function<String, T> function) {
		return context.getOption(key)
				.map(OptionMapping::getAsString)
				.map(function);
	}

	protected static @NonNull String requireStringOption(@NonNull final CommandContext context, @NonNull final String key) throws CommandIncompleteException {
		return nullableStringOption(context, key)
				.orElseThrow(CommandIncompleteException::new);
	}

	protected static @NonNull <T> T requireStringOption(@NonNull final CommandContext context, @NonNull final String key, @NonNull final Function<String, T> function) throws CommandIncompleteException {
		return nullableStringOption(context, key, function)
				.orElseThrow(CommandIncompleteException::new);
	}

	/* ========== INTEGER OPTIONS ========== */
	protected static @NonNull Optional<Integer> nullableIntegerOption(@NonNull final CommandContext context, @NonNull final String key) {
		return context.getOption(key)
				.map(OptionMapping::getAsInt);
	}

	protected static @NonNull <T> Optional<T> nullableIntegerOption(@NonNull final CommandContext context, @NonNull final String key, @NonNull final Function<Integer, T> function) {
		return context.getOption(key)
				.map(OptionMapping::getAsInt)
				.map(function);
	}

	protected static int requireIntegerOption(@NonNull final CommandContext context, @NonNull final String key) throws CommandIncompleteException {
		return nullableIntegerOption(context, key)
				.orElseThrow(CommandIncompleteException::new);
	}

	protected static @NonNull <T> T requireIntegerOption(@NonNull final CommandContext context, @NonNull final String key, @NonNull final Function<Integer, T> function) throws CommandIncompleteException {
		return nullableIntegerOption(context, key, function)
				.orElseThrow(CommandIncompleteException::new);
	}

	/* ========== BOOLEAN OPTIONS ========== */
	protected static @NonNull Optional<Boolean> nullableBooleanOption(@NonNull final CommandContext context, @NonNull final String key) {
		return context.getOption(key)
				.map(OptionMapping::getAsBoolean);
	}

	protected static @NonNull <T> Optional<T> nullableBooleanOption(@NonNull final CommandContext context, @NonNull final String key, @NonNull final Function<Boolean, T> function) {
		return context.getOption(key)
				.map(OptionMapping::getAsBoolean)
				.map(function);
	}

	protected static boolean requireBooleanOption(@NonNull final CommandContext context, @NonNull final String key) throws CommandIncompleteException {
		return nullableBooleanOption(context, key)
				.orElseThrow(CommandIncompleteException::new);
	}

	protected static @NonNull <T> T requireBooleanOption(@NonNull final CommandContext context, @NonNull final String key, @NonNull final Function<Boolean, T> function) throws CommandIncompleteException {
		return nullableBooleanOption(context, key, function)
				.orElseThrow(CommandIncompleteException::new);
	}

	@Override
	public final @NonNull MessageEditData handle(@NonNull final SlashCommandInteractionEvent event) {
		try (final var _ = metrics.timer(getCommandId()).time()) {
			return compose(new CommandContext(event));
		} catch (IllegalArgumentException _) {
			return response("one_of_your_options_was_invalid");
		} catch (BotException e) {
			return response(e.getMessage());
		}
	}
}
