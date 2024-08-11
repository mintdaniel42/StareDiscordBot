package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.RestAction;

public interface CommandAdapter {
	// TODO DOCS
	@NonNull
	RestAction<?> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event);
}
