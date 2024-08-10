package org.mintdaniel42.starediscordbot.buttons;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.data.TutorialModel;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.Arrays;

@UtilityClass
public class TutorialButton extends ListenerAdapter {
	public @Nullable ActionRow create(@NonNull final TutorialModel tutorialModel) {
		if (tutorialModel.getSimilar().length > 0) {
			return ActionRow.of(Arrays.stream(tutorialModel.getSimilar())
					.map(s -> Button.primary(s, R.Tutorials.get(s).getTitle()))
					.toArray(ItemComponent[]::new));
		}
		return null;
	}
}
