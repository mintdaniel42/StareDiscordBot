package org.mintdaniel42.starediscordbot.compose.command;

import lombok.NonNull;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.mintdaniel42.starediscordbot.compose.ComposerHelper;
import org.mintdaniel42.starediscordbot.compose.exceptions.CommandIncompleteException;
import org.mintdaniel42.starediscordbot.data.repository.UsernameRepository;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class CommandHelper extends ComposerHelper {
	public CommandHelper(@NonNull final UsernameRepository usernameRepository) {
		super(usernameRepository);
	}

	/* ========== OPTIONS META ========== */
	protected final boolean requireOptionCount(@NonNull final CommandContext context, final int atLeast) {
		if (context.options().size() < atLeast) {
			throw new CommandIncompleteException();
		}
		return true;
	}

	protected final @NonNull <T> T requireOptionCount(@NonNull final CommandContext context, final int atLeast, @NonNull final Supplier<T> supplier) {
		if (context.options().size() < atLeast) {
			throw new CommandIncompleteException();
		}
		return supplier.get();
	}

	/* ========== STRING OPTIONS ========== */
	protected final @NonNull String requireStringOption(@NonNull final CommandContext context, @NonNull final String key) {
		return context.options()
				.stream()
				.filter(Objects::nonNull)
				.filter(optionMapping -> optionMapping.getName().equals(key))
				.findFirst()
				.map(OptionMapping::getAsString)
				.orElseThrow(CommandIncompleteException::new);
	}

	protected final @NonNull <T> T requireStringOption(@NonNull final CommandContext context, @NonNull final String key, @NonNull final Function<String, T> function) {
		return context.options()
				.stream()
				.filter(Objects::nonNull)
				.filter(optionMapping -> optionMapping.getName().equals(key))
				.findFirst()
				.map(OptionMapping::getAsString)
				.map(function)
				.orElseThrow(CommandIncompleteException::new);
	}

	protected final @NonNull Optional<String> nullableStringOption(@NonNull final CommandContext context, @NonNull final String key) {
		return context.options()
				.stream()
				.filter(Objects::nonNull)
				.filter(optionMapping -> optionMapping.getName().equals(key))
				.findFirst()
				.map(OptionMapping::getAsString);
	}

	protected final @NonNull <T> Optional<T> nullableStringOption(@NonNull final CommandContext context, @NonNull final String key, @NonNull final Function<String, T> function) {
		return context.options()
				.stream()
				.filter(Objects::nonNull)
				.filter(optionMapping -> optionMapping.getName().equals(key))
				.findFirst()
				.map(OptionMapping::getAsString)
				.map(function);
	}

	/* ========== BOOLEAN OPTIONS ========== */
	protected final boolean requireBooleanOption(@NonNull final CommandContext context, @NonNull final String key) {
		return context.options()
				.stream()
				.filter(Objects::nonNull)
				.filter(optionMapping -> optionMapping.getName().equals(key))
				.findFirst()
				.map(OptionMapping::getAsBoolean)
				.orElseThrow(CommandIncompleteException::new);
	}

	protected final @NonNull <T> T requireBooleanOption(@NonNull final CommandContext context, @NonNull final String key, @NonNull final Function<Boolean, T> function) {
		return context.options()
				.stream()
				.filter(Objects::nonNull)
				.filter(optionMapping -> optionMapping.getName().equals(key))
				.findFirst()
				.map(OptionMapping::getAsBoolean)
				.map(function)
				.orElseThrow(CommandIncompleteException::new);
	}

	protected final @NonNull Optional<Boolean> nullableBooleanOption(@NonNull final CommandContext context, @NonNull final String key) {
		return context.options()
				.stream()
				.filter(Objects::nonNull)
				.filter(optionMapping -> optionMapping.getName().equals(key))
				.findFirst()
				.map(OptionMapping::getAsBoolean);
	}

	protected final @NonNull <T> Optional<T> nullableBooleanOption(@NonNull final CommandContext context, @NonNull final String key, @NonNull final Function<Boolean, T> function) {
		return context.options()
				.stream()
				.filter(Objects::nonNull)
				.filter(optionMapping -> optionMapping.getName().equals(key))
				.findFirst()
				.map(OptionMapping::getAsBoolean)
				.map(function);
	}
}
