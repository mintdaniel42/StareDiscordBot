package org.mintdaniel42.starediscordbot.commands.user;

import io.avaje.inject.RequiresProperty;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.mintdaniel42.starediscordbot.utils.R;

@Named("user")
@RequiresProperty(value = "feature.command.user.enabled", equalTo = "true")
@Singleton
public final class UserCommand extends CommandDataImpl {
	public UserCommand() {
		super("user", R.Strings.ui("user_related_commands"));
	}
}
