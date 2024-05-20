package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.GroupModel;
import org.mintdaniel42.starediscordbot.db.RequestModel;
import org.mintdaniel42.starediscordbot.utils.DCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.LongStream;

@RequiredArgsConstructor
public final class AutoCompletionHandler extends ListenerAdapter {
	@NonNull private final DatabaseAdapter databaseAdapter;

	@Override
	public void onCommandAutoCompleteInteraction(@NonNull final CommandAutoCompleteInteractionEvent event) {
		switch (event.getFocusedOption().getName()) {
			case String option when option.equals("username") || option.equals("leader") -> {
				OptionMapping usernameMapping = event.getOption(option);
				if (usernameMapping != null) event.replyChoiceStrings(DCHelper.autoCompleteUsername(databaseAdapter, usernameMapping.getAsString())).queue();
				else event.replyChoices().queue();
			} case "points" -> {
				OptionMapping pointsMapping = event.getOption("points");
				if (pointsMapping != null && !pointsMapping.getAsString().isBlank()) event.replyChoices(DCHelper.autocompleteDouble(pointsMapping.getAsString())).queue();
				else event.replyChoices().queue();
			} case "id" -> {
				long now = Instant.now().toEpochMilli();
				OptionMapping idMapping = event.getOption("id");
				String id = idMapping != null ? idMapping.getAsString() : "";
				event.replyChoiceLongs(Objects.requireNonNull(databaseAdapter.getPendingRequests())
								.stream()
								.map(RequestModel::getTimestamp)
								.filter(timestamp -> timestamp > now - Options.getMaxRequestAge())
								.filter(timestamp -> String.valueOf(timestamp).startsWith(id))
								.limit(25)
								.toList())
						.queue();
			} case String option when option.equals("page") && event.getFullCommandName().equals("hns list") -> {
				OptionMapping pageMapping = event.getOption(option);
				String page = pageMapping != null ? pageMapping.getAsString() : "";
				event.replyChoiceLongs(LongStream.range(0, Math.min(databaseAdapter.getHnsPages(), 25))
						.map(operand -> operand + 1)
						.boxed()
						.filter(operand -> String.valueOf(operand).startsWith(page))
						.toList()).queue();
			} case String option when option.equals("page") && event.getFullCommandName().equals("pg list") -> {
				OptionMapping pageMapping = event.getOption(option);
				String page = pageMapping != null ? pageMapping.getAsString() : "";
				event.replyChoiceLongs(LongStream.range(0, Math.min(databaseAdapter.getPgPages(), 25))
						.map(operand -> operand + 1)
						.boxed()
						.filter(operand -> String.valueOf(operand).startsWith(page))
						.toList()).queue();
			} case "tag" -> {
				OptionMapping tagMapping = event.getOption("tag");
				if (tagMapping != null) {
					List<GroupModel> groupModels = databaseAdapter.getGroups(tagMapping.getAsString().toLowerCase(Options.getLocale()));
					if (groupModels != null) {
						event.replyChoices(groupModels.stream()
								.limit(25)
								.map(groupModel -> new Command.Choice(groupModel.getName(), groupModel.getTag()))
								.toList())
								.queue();
					} else event.replyChoices().queue();
				} else event.replyChoices().queue();
			} default -> event.replyChoices().queue();
		}
	}
}
