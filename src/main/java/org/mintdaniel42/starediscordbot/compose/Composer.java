package org.mintdaniel42.starediscordbot.compose;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.PropertyKey;
import org.mintdaniel42.starediscordbot.BotConfig;
import org.mintdaniel42.starediscordbot.compose.exception.NoSuchChannelException;
import org.mintdaniel42.starediscordbot.compose.exception.NoSuchEntryException;
import org.mintdaniel42.starediscordbot.compose.exception.NoSuchPageException;
import org.mintdaniel42.starediscordbot.compose.exception.UnknownUsernameException;
import org.mintdaniel42.starediscordbot.data.entity.ProfileEntity;
import org.mintdaniel42.starediscordbot.data.repository.BaseRepository;
import org.mintdaniel42.starediscordbot.data.repository.ProfileRepository;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Permission;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.Optional;
import java.util.function.Function;

public abstract class Composer<CONTEXT extends Context> {
	/* ========== ENTITIES ========== */
	protected static @NonNull <ID, ENTITY> ENTITY requireEntity(@NonNull final BaseRepository<ID, ENTITY> repository, @NonNull final ID id) throws NoSuchEntryException {
		return nullableEntity(repository, id)
				.orElseThrow(NoSuchEntryException::new);
	}

	protected static @NonNull <ID, ENTITY, T> T requireEntity(@NonNull final BaseRepository<ID, ENTITY> repository, @NonNull final ID id, @NonNull final Function<ENTITY, T> function) throws NoSuchEntryException {
		return nullableEntity(repository, id, function)
				.orElseThrow(NoSuchEntryException::new);
	}

	protected static @NonNull <ID, ENTITY> Optional<ENTITY> nullableEntity(@NonNull final BaseRepository<ID, ENTITY> repository, @NonNull final ID id) {
		return repository.selectById(id);
	}

	protected static @NonNull <ID, ENTITY, T> Optional<T> nullableEntity(@NonNull final BaseRepository<ID, ENTITY> repository, @NonNull final ID id, @NonNull final Function<ENTITY, T> function) {
		return repository.selectById(id)
				.map(function);
	}

	/* ========== PROFILE ENTITY ========== */
	protected static @NonNull ProfileEntity requireProfile(@NonNull final ProfileRepository repository, @NonNull final String username) throws BotException {
		return Optional.ofNullable(MCHelper.getUuid(repository, username))
				.flatMap(repository::selectById)
				.orElseThrow(UnknownUsernameException::new);
	}

	/* ========== CHANNELS ========== */
	protected static <CONTEXT extends Context> TextChannel requireChannel(@NonNull final CONTEXT context, final long guildId, final long channelId) throws NoSuchChannelException {
		return context.getGuild(guildId)
				.map(guild -> guild.getTextChannelById(channelId))
				.orElseThrow(NoSuchChannelException::new);
	}

	/* ========== PERMISSIONS ========== */
	protected static boolean requirePermission(@NonNull final BotConfig config, @NonNull final Member member, @NonNull final Permission permission) {
		return Permission.fromUser(config, member).ordinal() >= permission.ordinal();
	}

	/* ========== OPERATIONS ========== */
	protected static void requireBounds(final int min, final int value, final int max) throws BotException {
		if (value >= max || value < min) throw new NoSuchPageException(); // TODO: make more general
	}

	/* ========== FACTORY METHOD FOR NEW MESSAGE EDIT BUILDER ========== */
	@Contract(value = " -> new", pure = true)
	protected static @NonNull MessageEditBuilder response() {
		return new MessageEditBuilder()
				.setReplace(true);
	}

	@Contract(value = "_, _ -> new", pure = true)
	protected static @NonNull MessageEditData response(@NonNull @PropertyKey(resourceBundle = "ui") final String message, @NonNull final Object... args) {
		return response()
				.setContent(R.Strings.ui(message, args))
				.build();
	}

	/* ========== PREVENT EXECUTION ========== */
	@Contract("_ -> fail")
	protected static <R> R fail(@NonNull @PropertyKey(resourceBundle = "ui") final String reason) throws BotException {
		throw new BotException(reason);
	}

	@NonNull
	protected abstract MessageEditData compose(@NonNull final CONTEXT context) throws BotException;
}
