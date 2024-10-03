package org.mintdaniel42.starediscordbot.commands.hns.achievements;

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

@Named("hns achievements")
@RequiresBean(HNSCommand.class)
@RequiresProperty(value = "feature.command.hns.achievements.enabled", equalTo = "true")
@Singleton
public final class AchievementsGroup extends SubcommandGroupData {
	public AchievementsGroup() {
		super("achievements", R.Strings.ui("achievements_related_commands"));
	}

	@Inject
	public void register(@NonNull @Named("hns") final SlashCommandData command) {
		command.addSubcommandGroups(this);
	}
}
