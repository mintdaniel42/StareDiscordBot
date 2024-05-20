package org.mintdaniel42.starediscordbot.embeds;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.GroupModel;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

@UtilityClass
public class GroupEmbed {
	public @NonNull MessageEmbed of(@NonNull final DatabaseAdapter databaseAdapter, @NonNull final GroupModel groupModel) {
		EmbedBuilder embedBuilder = new EmbedBuilder();

		embedBuilder.setTitle(R.string("group_overview"));
		embedBuilder.setDescription(String.format("%s [%s]", groupModel.getName(), groupModel.getTag()));
		embedBuilder.setColor(Options.getColorNormal());
		embedBuilder.addField(R.string("group_leader"), MCHelper.getUsername(databaseAdapter, groupModel.getLeader()), true);
		embedBuilder.addField(R.string("group_relation"), R.string(groupModel.getRelation().name()), true);

		return embedBuilder.build();
	}
}
