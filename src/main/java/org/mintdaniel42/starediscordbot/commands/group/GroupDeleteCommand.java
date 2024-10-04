package org.mintdaniel42.starediscordbot.commands.group;

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
import org.mintdaniel42.starediscordbot.data.repository.GroupRepository;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.Permission;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
@RequiresBean(GroupCommand.class)
@RequiresProperty(value = "feature.command.group.delete.enabled", equalTo = "true")
@Singleton
public final class GroupDeleteCommand extends BaseComposeCommand {
	@NonNull private final GroupRepository groupRepository;

	@Override
	protected @NonNull MessageEditData compose(@NonNull final CommandContext context) throws BotException {
		final var tag = requireStringOption(context, "tag");
		groupRepository.deleteById(tag);
		return response()
				.setContent(R.Strings.ui("the_group_was_successfully_deleted"))
				.build();
	}

	@Inject
	public void register(@NonNull @Named("group") SlashCommandData command) {
		command.addSubcommands(new SubcommandData("delete", R.Strings.ui("delete_group"))
				.addOption(OptionType.STRING, "tag", R.Strings.ui("group_tag"), true, true));
	}

	@Override
	public @NonNull String getCommandId() {
		return "group delete";
	}

	@Override
	public boolean hasPermission(@NonNull final BotConfig config, @Nullable final Member member) {
		return Permission.hasP4(config, member);
	}
}
