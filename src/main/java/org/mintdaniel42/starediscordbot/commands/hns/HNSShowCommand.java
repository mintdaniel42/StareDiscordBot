package org.mintdaniel42.starediscordbot.commands.hns;

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
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.mintdaniel42.starediscordbot.buttons.misc.GroupButton;
import org.mintdaniel42.starediscordbot.buttons.misc.HNSShowButton;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.data.repository.GroupRepository;
import org.mintdaniel42.starediscordbot.data.repository.HNSUserRepository;
import org.mintdaniel42.starediscordbot.data.repository.ProfileRepository;
import org.mintdaniel42.starediscordbot.data.repository.UserRepository;
import org.mintdaniel42.starediscordbot.embeds.user.hns.HNSBasicUserEmbed;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
@RequiresBean(HNSCommand.class)
@RequiresProperty(value = "feature.command.hns.show.enabled", equalTo = "true")
@Singleton
public final class HNSShowCommand extends BaseComposeCommand {
	@NonNull private final HNSUserRepository hnsUserRepository;
	@NonNull private final UserRepository userRepository;
	@NonNull private final ProfileRepository profileRepository;
	@NonNull private final GroupRepository groupRepository;

	@Override
	protected @NonNull MessageEditData compose(@NonNull final CommandContext context) throws BotException {
		final var profile = requireProfile(profileRepository, requireStringOption(context, "username"));
		final var hnsUser = requireEntity(hnsUserRepository, profile.getUuid());
		final var user = requireEntity(userRepository, profile.getUuid());
		final var groupOptional = nullableEntity(groupRepository, user.getGroupTag());
		return response()
				.setEmbeds(HNSBasicUserEmbed.of(hnsUser, user, profile, false))
				.setComponents(ActionRow.of(HNSShowButton.create(HNSShowButton.Type.more, profile.getUuid()),
						groupOptional.map(GroupButton::create)
								.orElseGet(GroupButton::disabled)))
				.build();
	}

	@Inject
	public void register(@NonNull @Named("hns") final SlashCommandData command) {
		command.addSubcommands(new SubcommandData("show", R.Strings.ui("show_hide_n_seek_entry"))
				.addOption(OptionType.STRING, "username", R.Strings.ui("minecraft_username"), true, true)
		);
	}

	@Override
	public @NonNull String getCommandId() {
		return "hns show";
	}
}
