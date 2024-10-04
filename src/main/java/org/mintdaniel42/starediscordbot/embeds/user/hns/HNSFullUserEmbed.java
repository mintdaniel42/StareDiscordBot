package org.mintdaniel42.starediscordbot.embeds.user.hns;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.data.entity.HNSUserEntity;
import org.mintdaniel42.starediscordbot.data.entity.ProfileEntity;
import org.mintdaniel42.starediscordbot.utils.Formatter;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

@UtilityClass
public class HNSFullUserEmbed {
	@Contract(pure = true, value = "_, _, _ -> new")
	public @NonNull MessageEmbed of(@NonNull final HNSUserEntity hnsUser, @NonNull final ProfileEntity username, final boolean isRequest) {
		return new EmbedBuilder()
				.setDescription(username.getUsername())
				.setColor(isRequest ? Options.getColorRequest() : Options.getColorNormal())
				.setThumbnail(MCHelper.getThumbnail(hnsUser.getUuid()))
				.setTitle(R.Strings.ui("hide_n_seek_player_database"))
				.addField(R.Strings.ui("rating"), hnsUser.getRating(), true)
				.addField(R.Strings.ui("points"), Formatter.formatNumber(hnsUser.getPoints()), true)
				.addField(R.Strings.ui("joined"), hnsUser.getJoined(), true)
				.addField(R.Strings.ui("banned"), hnsUser.isBanned() ? "✅" : "❌", true)
				.addField(R.Strings.ui("cheating"), hnsUser.isCheating() ? "✅" : "❌", true)
				.addField(R.Strings.ui("top10"), hnsUser.getTop10(), true)
				.addField(R.Strings.ui("streak"), String.valueOf(hnsUser.getStreak()), true)
				.addField(R.Strings.ui("highest_rank"), hnsUser.getHighestRank(), true)
				.addField(R.Strings.ui("secondary"), hnsUser.isSecondary() ? "✅" : "❌", true)
				.build();
	}
}
