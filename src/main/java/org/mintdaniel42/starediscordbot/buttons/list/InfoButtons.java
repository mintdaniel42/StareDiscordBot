package org.mintdaniel42.starediscordbot.buttons.list;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.utils.R;

@UtilityClass
public class InfoButtons {
	@Contract(pure = true, value = "-> new")
	public static @NonNull ActionRow create() {
		return ActionRow.of(
				Button.link(
						"https://github.com/mintdaniel42/StareDiscordBot",
						R.Strings.ui("show_source_code")
				),
				Button.primary(
						"list:%s:%s".formatted("hns", 0),
						R.Strings.ui("list_hide_n_seek_entries")
				),
				Button.primary(
						"list:%s:%s".formatted("pg", 0),
						R.Strings.ui("list_partygames_entries")
				)
		);
	}
}
