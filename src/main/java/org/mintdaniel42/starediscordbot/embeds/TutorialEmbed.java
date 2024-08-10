package org.mintdaniel42.starediscordbot.embeds;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.mintdaniel42.starediscordbot.data.TutorialModel;

@UtilityClass
public class TutorialEmbed {
	public static @NonNull MessageEmbed of(@NonNull final TutorialModel tutorialModel) {
		return new EmbedBuilder()
				.setTitle(tutorialModel.getTitle())
				.setDescription(tutorialModel.getDescription())
				.setColor(tutorialModel.getColor())
				.setThumbnail(tutorialModel.getThumbnailUrl())
				.setImage(tutorialModel.getImageUrl())
				.build();
	}
}
