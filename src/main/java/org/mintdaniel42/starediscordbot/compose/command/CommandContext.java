package org.mintdaniel42.starediscordbot.compose.command;

import lombok.NonNull;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.List;

public record CommandContext(@NonNull List<OptionMapping> options) {}