package org.mintdaniel42.starediscordbot.commands.pg;

import io.avaje.inject.RequiresProperty;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.mintdaniel42.starediscordbot.utils.R;

@Named("pg")
@RequiresProperty(value = "feature.command.pg.enabled", equalTo = "true")
@Singleton
public final class PGCommand extends CommandDataImpl {
	public PGCommand() {
		super("pg", R.Strings.ui("partygames_related_commands"));
	}
}
