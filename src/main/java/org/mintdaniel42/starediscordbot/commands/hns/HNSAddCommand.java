package org.mintdaniel42.starediscordbot.commands.hns;

import io.avaje.inject.RequiresBean;
import io.avaje.inject.RequiresProperty;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.BotConfig;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.data.entity.HNSUserEntity;
import org.mintdaniel42.starediscordbot.data.entity.UserEntity;
import org.mintdaniel42.starediscordbot.data.repository.HNSUserRepository;
import org.mintdaniel42.starediscordbot.data.repository.ProfileRepository;
import org.mintdaniel42.starediscordbot.data.repository.UserRepository;
import org.mintdaniel42.starediscordbot.embeds.user.hns.HNSFullUserEmbed;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.Permission;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
@RequiresBean(HNSCommand.class)
@RequiresProperty(value = "feature.command.hns.add.enabled", equalTo = "true")
@Singleton
public final class HNSAddCommand extends BaseComposeCommand {
	@NonNull private final HNSUserRepository hnsUserRepository;
	@NonNull private final UserRepository userRepository;
	@NonNull private final ProfileRepository profileRepository;

	@Override
	protected @NonNull MessageEditData compose(@NonNull final CommandContext context) throws BotException {
		requireOptionCount(context, 2);
		final var profile = requireProfile(profileRepository, requireStringOption(context, "username"));
		final var hnsUser = merge(context, HNSUserEntity.builder().uuid(profile.getUuid()));
		final var user = nullableEntity(userRepository, profile.getUuid())
				.orElseGet(() -> UserEntity.builder()
						.uuid(profile.getUuid())
						.build());
		userRepository.upsert(user);
		hnsUserRepository.insert(hnsUser);
		return response()
				.setContent(R.Strings.ui("the_entry_was_successfully_created"))
				.setEmbeds(HNSFullUserEmbed.of(hnsUser, profile, false))
				.build();
	}

	@Inject
	public void register(@NonNull @Named("hns") SlashCommandData command) {
		command.addSubcommands(new SubcommandData("add", R.Strings.ui("add_a_new_hide_n_seek_entry"))
				.addOption(OptionType.STRING, "username", R.Strings.ui("minecraft_username"), true, true)
				.addOption(OptionType.NUMBER, "points", R.Strings.ui("points"), true, true)
				.addOption(OptionType.STRING, "rating", R.Strings.ui("rating"))
				.addOption(OptionType.STRING, "joined", R.Strings.ui("joined"))
				.addOption(OptionType.BOOLEAN, "secondary", R.Strings.ui("secondary"))
				.addOption(OptionType.BOOLEAN, "banned", R.Strings.ui("banned"))
				.addOption(OptionType.BOOLEAN, "cheating", R.Strings.ui("cheating"))
				.addOption(OptionType.STRING, "top10", R.Strings.ui("top10"))
				.addOption(OptionType.INTEGER, "streak", R.Strings.ui("streak"))
				.addOption(OptionType.STRING, "highest_rank", R.Strings.ui("highest_rank")));
	}

	@Override
	public @NonNull String getCommandId() {
		return "hns add";
	}

	@Override
	public boolean hasPermission(@NonNull final BotConfig config, @Nullable final Member member) {
		return Permission.hasP4(config, member);
	}
}
