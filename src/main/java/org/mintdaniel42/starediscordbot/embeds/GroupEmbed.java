package org.mintdaniel42.starediscordbot.embeds;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.GroupModel;
import org.mintdaniel42.starediscordbot.db.UserModel;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.List;

@UtilityClass
public class GroupEmbed {
	public @NonNull MessageEmbed of(@NonNull final DatabaseAdapter databaseAdapter, @NonNull final GroupModel groupModel, final int page) {
		EmbedBuilder builder = new EmbedBuilder();

		builder.setTitle(R.string("group_overview"));
		builder.setDescription(String.format("%s [%s]", groupModel.getName(), groupModel.getTag()));
		builder.setColor(Options.getColorNormal());
		builder.addField(R.string("group_leader"), MCHelper.getUsername(databaseAdapter, groupModel.getLeader()), false);
		builder.addField(R.string("group_relation"), R.string(groupModel.getRelation().name()), false);

		if (databaseAdapter.getGroupMembers(groupModel, page) instanceof List<UserModel> users) {
			users.forEach(user -> builder.addField(
					user.getUsername(),
					R.string("banned") + ": " + (user.getHnsUser().isBanned() ? "✅" : "❌"), true)
			);
		}

		return builder.build();
	}
}
