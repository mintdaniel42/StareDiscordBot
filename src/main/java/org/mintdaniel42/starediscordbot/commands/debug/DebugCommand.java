package org.mintdaniel42.starediscordbot.commands.debug;

import io.avaje.inject.RequiresProperty;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.mintdaniel42.starediscordbot.utils.R;

@Named("debug")
@RequiresProperty(value = "feature.command.debug.enabled", equalTo = "true")
@Singleton
public final class DebugCommand extends CommandDataImpl {
	public DebugCommand() {
		super("debug", R.Strings.ui("debugging_commands"));
	}
}
