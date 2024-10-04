package org.mintdaniel42.starediscordbot.embeds.user.pg;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.data.entity.PGUserEntity;
import org.mintdaniel42.starediscordbot.data.entity.ProfileEntity;
import org.mintdaniel42.starediscordbot.utils.Formatter;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

@UtilityClass
public class PGUserEmbed {
	@Contract(pure = true, value = "_, _, _ -> new")
	public @NonNull MessageEmbed of(@NonNull final PGUserEntity pgUser, @NonNull final ProfileEntity username, final boolean isRequest) {
		return new EmbedBuilder()
				.setDescription(username.getUsername())
				.setColor(isRequest ? Options.getColorRequest() : Options.getColorNormal())
				.setThumbnail(MCHelper.getThumbnail(pgUser.getUuid()))
				.setTitle(R.Strings.ui("partygames_player_database"))
				.setThumbnail(MCHelper.getThumbnail(pgUser.getUuid()))
				.addField(R.Strings.ui("rating"), pgUser.getRating(), true)
				.addField(R.Strings.ui("points"), Formatter.formatNumber(pgUser.getPoints()), true)
				.addField(R.Strings.ui("joined"), pgUser.getJoined(), true)
				.addField(R.Strings.ui("luck"), String.valueOf(pgUser.getLuck()), true)
				.addField(R.Strings.ui("quota"), String.format("%s%%", pgUser.getQuota()), true)
				.addField(R.Strings.ui("winrate"), String.format("%s%%", pgUser.getWinrate()), true)
				.build();
	}
}
