package org.mintdaniel42.starediscordbot.commands.pg;

import io.avaje.inject.RequiresBean;
import io.avaje.inject.RequiresProperty;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.mintdaniel42.starediscordbot.buttons.misc.GroupButton;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.data.repository.GroupRepository;
import org.mintdaniel42.starediscordbot.data.repository.PGUserRepository;
import org.mintdaniel42.starediscordbot.data.repository.ProfileRepository;
import org.mintdaniel42.starediscordbot.data.repository.UserRepository;
import org.mintdaniel42.starediscordbot.embeds.user.pg.PGUserEmbed;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
@RequiresBean(PGCommand.class)
@RequiresProperty(value = "feature.command.pg.show.enabled", equalTo = "true")
@Singleton
public final class PGShowCommand extends BaseComposeCommand {
	@NonNull private final PGUserRepository pgUserRepository;
	@NonNull private final UserRepository userRepository;
	@NonNull private final ProfileRepository profileRepository;
	@NonNull private final GroupRepository groupRepository;

	@Override
	protected @NonNull MessageEditData compose(@NonNull final CommandContext context) throws BotException {
		final var profile = requireProfile(profileRepository, requireStringOption(context, "username"));
		return response()
				.addEmbed(PGUserEmbed.of(requireEntity(pgUserRepository, profile.getUuid()), requireEntity(profileRepository, profile.getUuid()), false))
				.addComponent(nullableEntity(groupRepository, requireEntity(userRepository, profile.getUuid()).getGroupTag())
						.map(GroupButton::create)
						.orElseGet(GroupButton::disabled))
				.compose();
	}

	@Inject
	public void register(@NonNull @Named("pg") SlashCommandData command) {
		command.addSubcommands(new SubcommandData("show", R.Strings.ui("show_partygames_entry"))
				.addOption(OptionType.STRING, "username", R.Strings.ui("minecraft_username"), true, true));
	}

	@Override
	public @NonNull String getCommandId() {
		return "pg show";
	}
}
