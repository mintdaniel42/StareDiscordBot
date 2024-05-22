package org.mintdaniel42.starediscordbot.embeds;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.ICommandReference;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.List;

@UtilityClass
public class HelpEmbed {
	public MessageEmbed of(@NonNull final List<ICommandReference> commandReferences, final int page) {
		var builder = new EmbedBuilder()
				.setTitle(R.string("help"))
				.setDescription(R.string("you_can_find_all_commands_and_their_descriptions_here"))
				.setColor(Options.getColorNormal());

		commandReferences.stream()
				.skip((long) page * BuildConfig.entriesPerPage)
				.limit(BuildConfig.entriesPerPage)
				.forEach(commandReference -> builder.addField(commandReference.getName(), commandReference.getFullCommandName(), false));

		return builder.build();
	}
}
