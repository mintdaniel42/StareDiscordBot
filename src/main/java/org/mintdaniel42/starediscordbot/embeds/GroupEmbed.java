package org.mintdaniel42.starediscordbot.embeds;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.data.entity.GroupEntity;
import org.mintdaniel42.starediscordbot.data.entity.HNSUserEntity;
import org.mintdaniel42.starediscordbot.data.repository.HNSUserRepository;
import org.mintdaniel42.starediscordbot.data.repository.ProfileRepository;
import org.mintdaniel42.starediscordbot.data.repository.UserRepository;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

@UtilityClass
public class GroupEmbed {
	public @NonNull MessageEmbed of(@NonNull final GroupEntity group, @NonNull final UserRepository userRepository, @NonNull final HNSUserRepository hnsUserRepository, @NonNull final ProfileRepository profileRepository) throws BotException {
		return of(group, userRepository, hnsUserRepository, profileRepository, 0, false);
	}

	public @NonNull MessageEmbed of(@NonNull final GroupEntity group, @NonNull final UserRepository userRepository, @NonNull final HNSUserRepository hnsUserRepository, @NonNull final ProfileRepository profileRepository, final boolean isRequest) throws BotException {
		return of(group, userRepository, hnsUserRepository, profileRepository, 0, isRequest);
	}

	public @NonNull MessageEmbed of(@NonNull final GroupEntity group, @NonNull final UserRepository userRepository, @NonNull final HNSUserRepository hnsUserRepository, @NonNull final ProfileRepository profileRepository, final int page, final boolean isRequest) throws BotException {
		final var builder = new EmbedBuilder()
				.setTitle(R.Strings.ui("group_overview"))
				.setDescription(String.format("%s [%s]", group.getName(), group.getTag()))
				.setColor(isRequest ? Options.getColorRequest() : Options.getColorNormal())
				.addField(R.Strings.ui("group_leader"), MCHelper.getUsername(profileRepository, group.getLeader()), false)
				.addField(R.Strings.ui("group_relation"), R.Strings.ui(group.getRelation().name()), false);

		userRepository.selectByGroupTag(group.getTag()).stream()
				.skip((long) page * BuildConfig.entriesPerPage)
				.limit(BuildConfig.entriesPerPage)
				.forEach(user -> builder.addField(
						profileRepository.selectById(user.getUuid())
								.orElseThrow()
								.getUsername(),
						R.Strings.ui("banned") + ": " + (hnsUserRepository.selectById(user.getUuid())
								.map(HNSUserEntity::isBanned).
								orElse(false) ? "✅" : "❌"), false)
				);

		return builder.build();
	}
}
