package org.mintdaniel42.starediscordbot.buttons;

import lombok.NonNull;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.data.TutorialModel;
import org.mintdaniel42.starediscordbot.embeds.TutorialEmbed;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.Arrays;

public class TutorialButton extends ListenerAdapter {
	public static @Nullable ActionRow create(@NonNull final TutorialModel tutorialModel) {
		if (tutorialModel.getSimilar().length > 0) {
			return ActionRow.of(Arrays.stream(tutorialModel.getSimilar())
					.map(s -> Button.primary("tutorial:" + s, R.Tutorials.get(s).getTitle()))
					.toArray(ItemComponent[]::new));
		}
		return null;
	}

	@Override
	public void onButtonInteraction(@NonNull final ButtonInteractionEvent event) {
		String[] buttonParts = event.getComponentId().split(":");
		if (buttonParts[0].equals("tutorial") && buttonParts.length == 2) {
			if (!Options.isInMaintenance()) {
				if (R.Tutorials.get(buttonParts[1]) instanceof TutorialModel tutorialModel) {
					final var actionBar = TutorialButton.create(tutorialModel);
					final var callback = event.replyEmbeds(TutorialEmbed.of(tutorialModel));
					if (actionBar != null) callback.addComponents(actionBar);
					callback.queue();
				} else event.reply(R.Strings.ui("this_page_does_not_exist")).queue();
			} else event.reply(R.Strings.ui("the_bot_is_currently_in_maintenance_mode")).queue();
		}
	}
}
