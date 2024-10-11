package org.mintdaniel42.starediscordbot.commands.hns.maps;

import io.avaje.inject.RequiresBean;
import io.avaje.inject.RequiresProperty;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.mintdaniel42.starediscordbot.commands.hns.HNSCommand;
import org.mintdaniel42.starediscordbot.utils.R;

@Named("hns maps")
@RequiresBean(HNSCommand.class)
@RequiresProperty(value = "feature.command.hns.maps.enabled", equalTo = "true")
@Singleton
public final class MapsGroup extends SubcommandGroupData {
	public MapsGroup() {
		super("maps", R.Strings.ui("maps_related_command"));
	}

	@Inject
	public void register(@NonNull @Named("hns") final SlashCommandData command) {
		command.addSubcommandGroups(this);
	}
}
