package org.mintdaniel42.starediscordbot.embeds;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.data.GroupModel;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

@UtilityClass
public class GroupEmbed {
	public @NonNull MessageEmbed of(@NonNull final DatabaseAdapter databaseAdapter, @NonNull final GroupModel groupModel, final int page) {
		return of(databaseAdapter, groupModel, page, false);
	}

	public @NonNull MessageEmbed of(@NonNull final DatabaseAdapter databaseAdapter, @NonNull final GroupModel groupModel, final int page, final boolean isRequest) {
		EmbedBuilder builder = new EmbedBuilder()
				.setTitle(R.Strings.ui("group_overview"))
				.setDescription(String.format("%s [%s]", groupModel.getName(), groupModel.getTag()))
				.setColor(isRequest ? Options.getColorRequest() : Options.getColorNormal())
				.addField(R.Strings.ui("group_leader"), MCHelper.getUsername(databaseAdapter, groupModel.getLeader()), false)
				.addField(R.Strings.ui("group_relation"), R.Strings.ui(groupModel.getRelation().name()), false);

		groupModel.getMembers().stream()
				.skip((long) page * BuildConfig.entriesPerPage)
				.limit(BuildConfig.entriesPerPage)
				.forEach(user -> builder.addField(
						user.getUsername(),
						R.Strings.ui("banned") + ": " + (user.getHnsUser().isBanned() ? "✅" : "❌"), false)
				);

		return builder.build();
	}
}
