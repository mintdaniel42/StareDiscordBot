package org.mintdaniel42.starediscordbot.compose.button;

import lombok.NonNull;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.mintdaniel42.starediscordbot.compose.Context;

public class ButtonContext extends Context {
	@NonNull final String[] buttonParts;

	public ButtonContext(@NonNull final ButtonInteractionEvent event) {
		if (event.getMember() == null) throw new IllegalStateException();
		super(event.getJDA(), event.getMember());
		buttonParts = event.getComponentId().split(":");
	}

	public String getButtonPartAt(final int index) {
		return buttonParts[index];
	}

	public int getButtonPartsLength() {
		return buttonParts.length;
	}
}
