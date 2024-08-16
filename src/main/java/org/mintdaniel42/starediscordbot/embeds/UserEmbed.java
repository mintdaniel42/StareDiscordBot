package org.mintdaniel42.starediscordbot.embeds;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.data.entity.GroupEntity;
import org.mintdaniel42.starediscordbot.data.entity.UserEntity;
import org.mintdaniel42.starediscordbot.data.entity.UsernameEntity;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

@UtilityClass
public class UserEmbed {
	@Contract(pure = true, value = "_, _, _, _ -> new")
	public @NonNull MessageEmbed of(@NonNull final UserEntity user, @Nullable final GroupEntity group, @NonNull final UsernameEntity username, final boolean isRequest) {
		return new EmbedBuilder()
				.setDescription(username.getUsername())
                .setColor(isRequest ? Options.getColorRequest() : Options.getColorNormal())
				.setThumbnail(MCHelper.getThumbnail(user.getUuid()))
				.setTitle(R.Strings.ui("user_base_info"))
				.addField(R.Strings.ui("group_name"), group == null ? "❌" : group.getName(), true)
				.addField(R.Strings.ui("discord_tag"), user.getDiscord() == 0 ? "❌" : "<@%s>".formatted(user.getDiscord()), true)
				.addField(R.Strings.ui("note"), user.getNote(), false)
				.build();
    }
}
