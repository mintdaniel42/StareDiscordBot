package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.GroupModel;
import org.mintdaniel42.starediscordbot.db.RequestModel;
import org.mintdaniel42.starediscordbot.utils.DCHelper;
import org.mintdaniel42.starediscordbot.utils.R;

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
				if (event.getOption("points") instanceof OptionMapping pointsMapping &&
						!pointsMapping.getAsString().isBlank()) {
					event.replyChoices(DCHelper.autocompleteDouble(pointsMapping.getAsString())).queue();
				} else event.replyChoices().queue();
			} case "id" -> {
				if (event.getOption("id") instanceof OptionMapping idMapping && !idMapping.getAsString().isBlank()) {
					String id = idMapping.getAsString();
					event.replyChoiceLongs(Objects.requireNonNull(databaseAdapter.getPendingRequests())
									.stream()
									.map(RequestModel::getTimestamp)
									.filter(timestamp -> String.valueOf(timestamp).startsWith(id))
									.limit(25)
									.toList())
							.queue();
				} else event.replyChoices().queue();
			} case String option when option.equals("page") && event.getFullCommandName().equals("hns list") -> {
				if (event.getOption("page") instanceof OptionMapping pageMapping && !pageMapping.getAsString().isBlank()) {
					event.replyChoiceLongs(LongStream.range(0, Math.min(databaseAdapter.getHnsPages(), 25))
							.map(operand -> operand + 1)
							.boxed()
							.filter(operand -> String.valueOf(operand).startsWith(pageMapping.getAsString()))
							.toList()).queue();
				} else event.replyChoices().queue();
			} case String option when option.equals("page") && event.getFullCommandName().equals("pg list") -> {
				if (event.getOption("page") instanceof OptionMapping pageMapping && !pageMapping.getAsString().isBlank()) {
					event.replyChoiceLongs(LongStream.range(0, Math.min(databaseAdapter.getPgPages(), 25))
							.map(operand -> operand + 1)
							.boxed()
							.filter(operand -> String.valueOf(operand).startsWith(pageMapping.getAsString()))
							.toList()).queue();
				} else event.replyChoices().queue();
			} case "tag" -> {
				if (event.getOption("tag") instanceof OptionMapping tagMapping) {
					List<GroupModel> groupModels = databaseAdapter.getGroups(tagMapping.getAsString().toLowerCase(BuildConfig.locale));
					if (groupModels != null) {
						event.replyChoices(groupModels.stream()
										.limit(25)
										.map(groupModel -> new Command.Choice(groupModel.getName(), groupModel.getTag()))
										.toList())
								.queue();
					} else event.replyChoices().queue();
				} else event.replyChoices().queue();
			} case "luck" -> {
				if (event.getOption("winrate") instanceof OptionMapping winrateMapping &&
						event.getOption("quota") instanceof OptionMapping quotaMapping) {
					event.replyChoice(R.Strings.ui("calculated_value_for_s_s",
									R.Strings.ui("luck"),
									quotaMapping.getAsDouble() - winrateMapping.getAsDouble()),
							quotaMapping.getAsDouble() - winrateMapping.getAsDouble()).queue();
				}
			}
			default -> event.replyChoices().queue();
		}
	}
}
