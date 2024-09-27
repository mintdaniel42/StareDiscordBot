package org.mintdaniel42.starediscordbot.embeds.user.hns;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.data.entity.GroupEntity;
import org.mintdaniel42.starediscordbot.data.entity.HNSUserEntity;
import org.mintdaniel42.starediscordbot.data.entity.ProfileEntity;
import org.mintdaniel42.starediscordbot.data.entity.UserEntity;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

@UtilityClass
public class HNSMoreUserEmbed {
	@Contract(pure = true, value = "_, _, _, _, _ -> new")
	public @NonNull MessageEmbed of(@NonNull final HNSUserEntity hnsUser, @NonNull final UserEntity user, @Nullable final GroupEntity group, @NonNull final ProfileEntity username, final boolean isRequest) {
		return new EmbedBuilder()
				.setDescription(username.getUsername())
				.setColor(isRequest ? Options.getColorRequest() : Options.getColorNormal())
				.setThumbnail(MCHelper.getThumbnail(hnsUser.getUuid()))
				.setTitle(R.Strings.ui("hide_n_seek_player_database_more_information"))
				.addField(R.Strings.ui("top10"), hnsUser.getTop10(), true)
				.addField(R.Strings.ui("streak"), String.valueOf(hnsUser.getStreak()), true)
				.addField(R.Strings.ui("highest_rank"), hnsUser.getHighestRank(), true)
				.addField(R.Strings.ui("secondary"), hnsUser.isSecondary() ? "✅" : "❌", true)
				.addField(R.Strings.ui("discord_tag"), user.getDiscord() == 0 ? "❌" : "<@%s>".formatted(user.getDiscord()), true)
				.addField(R.Strings.ui("group_name"), group == null ? "❌" : group.getName(), true)
				.build();
	}
}
