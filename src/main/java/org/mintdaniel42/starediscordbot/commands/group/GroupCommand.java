package org.mintdaniel42.starediscordbot.commands.group;

import io.avaje.inject.RequiresProperty;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.mintdaniel42.starediscordbot.utils.R;

@Named("group")
@RequiresProperty(value = "feature.command.group.enabled", equalTo = "true")
@Singleton
public final class GroupCommand extends CommandDataImpl {
	public GroupCommand() {
		super("group", R.Strings.ui("group_related_commands"));
	}
}
