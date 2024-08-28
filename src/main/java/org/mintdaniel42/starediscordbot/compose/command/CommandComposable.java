package org.mintdaniel42.starediscordbot.compose.command;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.commands.CommandAdapter;
import org.mintdaniel42.starediscordbot.compose.Composable;
import org.mintdaniel42.starediscordbot.compose.exceptions.CommandIncompleteException;
import org.mintdaniel42.starediscordbot.compose.exceptions.NoSuchEntryException;
import org.mintdaniel42.starediscordbot.compose.exceptions.UnknownUsernameException;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface CommandComposable extends Composable<CommandContext>, CommandAdapter {
	@Override
	default @NonNull WebhookMessageEditAction<Message> handle(@NonNull InteractionHook interactionHook, @NonNull SlashCommandInteractionEvent event) {
		try {
			return interactionHook.editOriginal(compose(new CommandContext(event.getOptions())));
		} catch (IllegalArgumentException _) {
			return interactionHook.editOriginal(R.Strings.ui("one_of_your_options_was_invalid"));
		} catch (CommandIncompleteException _) {
			return interactionHook.editOriginal(R.Strings.ui("your_command_was_incomplete"));
		} catch (UnknownUsernameException _) {
			return interactionHook.editOriginal(R.Strings.ui("this_username_does_not_exist"));
		} catch (NoSuchEntryException _) {
			return interactionHook.editOriginal(R.Strings.ui("we_could_not_find_such_an_entry"));
		}
	}

	/* ========== OPTIONS META ========== */
	default boolean requireOptionCount(@NonNull final CommandContext context, final int atLeast) {
		if (context.options().size() < atLeast) {
			throw new CommandIncompleteException();
		}
		return true;
	}

	default @NonNull <T> T requireOptionCount(@NonNull final CommandContext context, final int atLeast, @NonNull final Supplier<T> supplier) {
		if (context.options().size() < atLeast) {
			throw new CommandIncompleteException();
		}
		return supplier.get();
	}

	/* ========== STRING OPTIONS ========== */
	default @NonNull String requireStringOption(@NonNull final CommandContext context, @NonNull final String key) {
		return context.options()
				.stream()
				.filter(Objects::nonNull)
				.filter(optionMapping -> optionMapping.getName().equals(key))
				.findFirst()
				.map(OptionMapping::getAsString)
				.orElseThrow(CommandIncompleteException::new);
	}

	default @NonNull <T> T requireStringOption(@NonNull final CommandContext context, @NonNull final String key, @NonNull final Function<String, T> function) {
		return context.options()
				.stream()
				.filter(Objects::nonNull)
				.filter(optionMapping -> optionMapping.getName().equals(key))
				.findFirst()
				.map(OptionMapping::getAsString)
				.map(function)
				.orElseThrow(CommandIncompleteException::new);
	}

	default @NonNull Optional<String> nullableStringOption(@NonNull final CommandContext context, @NonNull final String key) {
		return context.options()
				.stream()
				.filter(Objects::nonNull)
				.filter(optionMapping -> optionMapping.getName().equals(key))
				.findFirst()
				.map(OptionMapping::getAsString);
	}

	default @NonNull <T> Optional<T> nullableStringOption(@NonNull final CommandContext context, @NonNull final String key, @NonNull final Function<String, T> function) {
		return context.options()
				.stream()
				.filter(Objects::nonNull)
				.filter(optionMapping -> optionMapping.getName().equals(key))
				.findFirst()
				.map(OptionMapping::getAsString)
				.map(function);
	}

	/* ========== BOOLEAN OPTIONS ========== */
	default boolean requireBooleanOption(@NonNull final CommandContext context, @NonNull final String key) {
		return context.options()
				.stream()
				.filter(Objects::nonNull)
				.filter(optionMapping -> optionMapping.getName().equals(key))
				.findFirst()
				.map(OptionMapping::getAsBoolean)
				.orElseThrow(CommandIncompleteException::new);
	}

	default @NonNull <T> T requireBooleanOption(@NonNull final CommandContext context, @NonNull final String key, @NonNull final Function<Boolean, T> function) {
		return context.options()
				.stream()
				.filter(Objects::nonNull)
				.filter(optionMapping -> optionMapping.getName().equals(key))
				.findFirst()
				.map(OptionMapping::getAsBoolean)
				.map(function)
				.orElseThrow(CommandIncompleteException::new);
	}

	default @NonNull Optional<Boolean> nullableBooleanOption(@NonNull final CommandContext context, @NonNull final String key) {
		return context.options()
				.stream()
				.filter(Objects::nonNull)
				.filter(optionMapping -> optionMapping.getName().equals(key))
				.findFirst()
				.map(OptionMapping::getAsBoolean);
	}

	default @NonNull <T> Optional<T> nullableBooleanOption(@NonNull final CommandContext context, @NonNull final String key, @NonNull final Function<Boolean, T> function) {
		return context.options()
				.stream()
				.filter(Objects::nonNull)
				.filter(optionMapping -> optionMapping.getName().equals(key))
				.findFirst()
				.map(OptionMapping::getAsBoolean)
				.map(function);
	}
}
