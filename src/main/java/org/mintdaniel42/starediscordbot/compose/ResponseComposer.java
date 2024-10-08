package org.mintdaniel42.starediscordbot.compose;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.PropertyKey;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.ArrayList;
import java.util.List;

public class ResponseComposer {
	@NonNull private final List<MessageEmbed> embeds = new ArrayList<>();
	@NonNull private final List<LayoutComponent> components = new ArrayList<>();
	@Nullable private String text;

	public @NonNull ResponseComposer setText(@NonNull @PropertyKey(resourceBundle = "ui") final String text, @NonNull Object... args) {
		this.text = R.Strings.ui(text, args);
		return this;
	}

	public @NonNull ResponseComposer addEmbed(@NonNull final MessageEmbed embed) {
		return addEmbed(embed, true);
	}

	public @NonNull ResponseComposer addEmbed(@NonNull final MessageEmbed embed, final boolean condition) {
		if (condition) embeds.add(embed);
		return this;
	}

	public @NonNull ResponseComposer addComponent(@NonNull final LayoutComponent component) {
		return addComponent(component, true);
	}

	public @NonNull ResponseComposer addComponent(@NonNull final LayoutComponent component, final boolean condition) {
		if (condition) components.add(component);
		return this;
	}

	public @NonNull ResponseComposer addComponent(@NonNull final ItemComponent component) {
		return addComponent(component, true);
	}

	public @NonNull ResponseComposer addComponent(@NonNull final ItemComponent component, final boolean condition) {
		if (condition) components.add(ActionRow.of(component));
		return this;
	}

	public MessageEditData compose() {
		return new MessageEditBuilder()
				.setReplace(true)
				.setEmbeds(embeds)
				.setContent(text)
				.setComponents(components)
				.build();
	}
}
