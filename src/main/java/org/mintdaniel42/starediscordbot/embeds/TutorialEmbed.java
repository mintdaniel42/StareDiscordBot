package org.mintdaniel42.starediscordbot.embeds;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.mintdaniel42.starediscordbot.data.entity.TutorialEntity;

@UtilityClass
public class TutorialEmbed {
	public static @NonNull MessageEmbed of(@NonNull final TutorialEntity tutorialEntity) {
		return new EmbedBuilder()
				.setTitle(tutorialEntity.getTitle())
				.setDescription(tutorialEntity.getDescription())
				.setColor(tutorialEntity.getColor())
				.setThumbnail(tutorialEntity.getThumbnailUrl())
				.setImage(tutorialEntity.getImageUrl())
				.build();
	}
}
