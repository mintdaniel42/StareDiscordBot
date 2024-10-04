package org.mintdaniel42.starediscordbot.commands.hns;

import io.avaje.inject.RequiresProperty;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.mintdaniel42.starediscordbot.utils.R;

@Named("hns")
@RequiresProperty(value = "feature.command.hns.enabled", equalTo = "true")
@Singleton
public final class HNSCommand extends CommandDataImpl {
	public HNSCommand() {
		super("hns", R.Strings.ui("hide_n_seek_related_commands"));
	}
}
