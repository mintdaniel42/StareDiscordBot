package org.mintdaniel42.starediscordbot.compose.command;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.commands.CommandAdapter;
import org.mintdaniel42.starediscordbot.compose.Composer;
import org.mintdaniel42.starediscordbot.compose.exception.CommandIncompleteException;
import org.mintdaniel42.starediscordbot.data.entity.GroupEntity;
import org.mintdaniel42.starediscordbot.data.entity.HNSUserEntity;
import org.mintdaniel42.starediscordbot.data.entity.PGUserEntity;
import org.mintdaniel42.starediscordbot.data.entity.UserEntity;
import org.mintdaniel42.starediscordbot.exception.BotException;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class BaseComposeCommand extends Composer<CommandContext> implements CommandAdapter {
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

	/* ========== MERGE ENTITIES ========== */
	@Contract(pure = true, value = "_, _, _ -> new")
	public static @NonNull GroupEntity merge(@NonNull final CommandContext context, final GroupEntity.EntityBuilder builder, @Nullable UUID leaderUuid) {
		for (OptionMapping optionMapping : context.getOptions()) {
			switch (optionMapping.getName()) {
				case "name" -> builder.name(optionMapping.getAsString());
				case "leader" -> {
					if (leaderUuid != null) builder.leader(leaderUuid);
				}
				case "relation" -> builder.relation(GroupEntity.Relation.valueOf(optionMapping.getAsString()));
			}
		}
		return builder.build();
	}

	@Contract(pure = true, value = "_, _ -> new")
	public static @NonNull HNSUserEntity merge(@NonNull final CommandContext context, final HNSUserEntity.EntityBuilder builder) {
		for (final var optionMapping : context.getOptions()) {
			switch (optionMapping.getName()) {
				case "rating" -> builder.rating(optionMapping.getAsString());
				case "points" -> builder.points(Math.round(optionMapping.getAsDouble()));
				case "joined" -> builder.joined(optionMapping.getAsString());
				case "secondary" -> builder.secondary(optionMapping.getAsBoolean());
				case "banned" -> builder.banned(optionMapping.getAsBoolean());
				case "cheating" -> builder.cheating(optionMapping.getAsBoolean());
				case "top10" -> builder.top10(optionMapping.getAsString());
				case "streak" -> builder.streak(optionMapping.getAsInt());
				case "highest_rank" -> builder.highestRank(optionMapping.getAsString());
			}
		}
		return builder.build();
	}

	@Contract(pure = true, value = "_, _ -> new")
	public static @NonNull PGUserEntity merge(@NonNull final CommandContext context, final PGUserEntity.EntityBuilder builder) {
		for (final var optionMapping : context.getOptions()) {
			switch (optionMapping.getName()) {
				case "rating" -> builder.rating(optionMapping.getAsString());
				case "points" -> builder.points(Math.round(optionMapping.getAsDouble()));
				case "joined" -> builder.joined(optionMapping.getAsString());
				case "luck" -> builder.luck(optionMapping.getAsDouble());
				case "quota" -> builder.quota(optionMapping.getAsDouble());
				case "winrate" -> builder.winrate(optionMapping.getAsDouble());
			}
		}
		return builder.build();
	}

	public static @NonNull UserEntity merge(@NonNull final CommandContext context, UserEntity.EntityBuilder builder) {
		for (OptionMapping optionMapping : context.getOptions()) {
			switch (optionMapping.getName()) {
				case "discord" -> builder.discord(optionMapping.getAsLong());
				case "note" -> builder.note(optionMapping.getAsString());
			}
		}
		return builder.build();
	}

	@Override
	public final @NonNull MessageEditData handle(@NonNull final SlashCommandInteractionEvent event) {
		try {
			return compose(new CommandContext(event));
		} catch (IllegalArgumentException _) {
			return response("one_of_your_options_was_invalid");
		} catch (BotException e) {
			return response(e.getMessage());
		}
	}
}
