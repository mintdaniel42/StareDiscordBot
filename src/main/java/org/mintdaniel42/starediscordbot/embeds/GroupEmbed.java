package org.mintdaniel42.starediscordbot.embeds;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.GroupModel;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

@UtilityClass
public class GroupEmbed {
	public @NonNull MessageEmbed of(@NonNull final DatabaseAdapter databaseAdapter, @NonNull final GroupModel groupModel, final int page) {
		EmbedBuilder builder = new EmbedBuilder()
				.setTitle(R.string("group_overview"))
				.setDescription(String.format("%s [%s]", groupModel.getName(), groupModel.getTag()))
				.setColor(Options.getColorNormal())
				.addField(R.string("group_leader"), MCHelper.getUsername(databaseAdapter, groupModel.getLeader()), false)
				.addField(R.string("group_relation"), R.string(groupModel.getRelation().name()), false);

		groupModel.getMembers().stream()
				.skip((long) page * BuildConfig.entriesPerPage)
				.limit((long) BuildConfig.entriesPerPage)
				.forEach(user -> builder.addField(
						user.getUsername(),
						R.string("banned") + ": " + (user.getHnsUser().isBanned() ? "✅" : "❌"), false)
				);

		return builder.build();
	}
}
