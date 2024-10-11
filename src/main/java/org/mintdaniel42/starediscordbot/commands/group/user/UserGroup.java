package org.mintdaniel42.starediscordbot.commands.group.user;

import io.avaje.inject.RequiresBean;
import io.avaje.inject.RequiresProperty;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.mintdaniel42.starediscordbot.commands.group.GroupCommand;
import org.mintdaniel42.starediscordbot.utils.R;

@Named("group user")
@RequiresBean(GroupCommand.class)
@RequiresProperty(value = "feature.command.group.user.enabled", equalTo = "true")
@Singleton
public final class UserGroup extends SubcommandGroupData {
	public UserGroup() {
		super("user", R.Strings.ui("user_related_group_commands"));
	}

	@Inject
	public void register(@NonNull @Named("group") final SlashCommandData command) {
		command.addSubcommandGroups(this);
	}
}