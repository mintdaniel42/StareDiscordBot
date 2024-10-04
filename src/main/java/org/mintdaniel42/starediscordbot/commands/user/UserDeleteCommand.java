package org.mintdaniel42.starediscordbot.commands.user;

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
import org.mintdaniel42.starediscordbot.data.Database;
import org.mintdaniel42.starediscordbot.data.repository.ProfileRepository;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.Permission;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
@RequiresBean(UserCommand.class)
@RequiresProperty(value = "feature.command.user.delete.enabled", equalTo = "true")
@Singleton
public final class UserDeleteCommand extends BaseComposeCommand {
	@NonNull private final Database database;
	@NonNull private final ProfileRepository profileRepository;

	@Override
	protected @NonNull MessageEditData compose(@NonNull final CommandContext context) throws BotException {
		final var profile = requireProfile(profileRepository, requireStringOption(context, "username"));
		database.deleteUserData(profile.getUuid());
		return response()
				.setContent(R.Strings.ui("the_user_s_was_successfully_deleted", profile.getUsername()))
				.build();
	}

	@Inject
	public void register(@NonNull @Named("user") SlashCommandData command) {
		command.addSubcommands(new SubcommandData("delete", R.Strings.ui("delete_a_user_entry"))
				.addOption(OptionType.STRING, "username", R.Strings.ui("minecraft_username"), true, true)
		);
	}

	@Override
	public @NonNull String getCommandId() {
		return "user delete";
	}

	@Override
	public boolean hasPermission(@NonNull final BotConfig config, @Nullable final Member member) {
		return Permission.hasP4(config, member);
	}
}
