package org.mintdaniel42.starediscordbot.buttons;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.utils.R;

@UtilityClass
public class InfoButtons extends ListenerAdapter {
	@Contract(pure = true, value = "-> new")
	public @NonNull ActionRow create() {
		return ActionRow.of(
				Button.primary(
						"info:%s:%s".formatted("hns", 0),
						R.Strings.ui("list_hide_n_seek_entries")
				),
				Button.primary(
						"info:%s:%s".formatted("pg", 0),
						R.Strings.ui("list_partygames_entries")
				)
		);
	}
}
