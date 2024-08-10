package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.data.GroupModel;
import org.mintdaniel42.starediscordbot.data.RequestModel;
import org.mintdaniel42.starediscordbot.data.UsernameModel;
import org.mintdaniel42.starediscordbot.utils.Calculator;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.LongStream;

@RequiredArgsConstructor
public final class AutoCompletionHandler extends ListenerAdapter {
	@NonNull private final DatabaseAdapter databaseAdapter;

	@Override
	public void onCommandAutoCompleteInteraction(@NonNull final CommandAutoCompleteInteractionEvent event) {
		final var focusedOptionValue = event.getFocusedOption().getValue().toLowerCase(BuildConfig.locale);
		event.replyChoices(switch (event.getFocusedOption().getName()) {
			case "username", "leader" -> autoCompleteUsername(focusedOptionValue);
			case "points" -> autoCompleteDouble(focusedOptionValue);
			case "id" -> autoCompleteId(focusedOptionValue);
			case "page" -> autoCompletePage(event.getFullCommandName(), focusedOptionValue);
			case "tag" -> autoCompleteTag(focusedOptionValue);
			case "luck" -> autoCompleteLuck(event.getOption("quota"), event.getOption("winrate"));
			default -> new Command.Choice[0];
		}).queue();
	}

	@Contract(pure = true, value = "_ -> new")
	private @NonNull Command.Choice[] autoCompleteUsername(@NonNull final String input) {
		if (databaseAdapter.getUsernames(input) instanceof List<UsernameModel> usernames) {
			return usernames.stream()
					.limit(25)
					.map(usernameModel -> new Command.Choice(usernameModel.getUsername(), usernameModel.getUsername()))
					.toArray(Command.Choice[]::new);
		} else return new Command.Choice[0];
	}

	@Contract(pure = true, value = "_ -> new")
	private @NonNull Command.Choice[] autoCompleteDouble(@NonNull final String input) {
		if (!input.isBlank()) {
			final var matcher = Pattern.compile("[+-]?((\\d+(\\.\\d*)?)|(\\.\\d+))").matcher(input);
			final var number = matcher.find() ? Double.parseDouble(matcher.group()) : 0;

			if (number <= 1_000_000) {
				return switch (input.charAt(input.length() - 1)) {
					case 'k' -> new Command.Choice[]{new Command.Choice(number + "K", number * 1_000)};
					case 'm' -> new Command.Choice[]{new Command.Choice(number + "M", number * 1_000_000)};
					case 'b' -> new Command.Choice[]{new Command.Choice(number + "B", number * 1_000_000_000)};
					default -> new Command.Choice[]{
							new Command.Choice(number + "K", number * 1_000),
							new Command.Choice(number + "M", number * 1_000_000),
							new Command.Choice(number + "B", number * 1_000_000_000)
					};
				};
			} else return new Command.Choice[0];
		} else return new Command.Choice[0];
	}

	@Contract(pure = true, value = "_ -> new")
	private @NonNull Command.Choice[] autoCompleteId(@NonNull final String input) {
		if (databaseAdapter.getPendingRequests() instanceof List<RequestModel> requestModels) {
			return requestModels.stream()
					.map(RequestModel::getTimestamp)
					.filter(timestamp -> String.valueOf(timestamp).startsWith(input))
					.limit(25)
					.map(timestamp -> new Command.Choice(String.valueOf(timestamp), timestamp))
					.toArray(Command.Choice[]::new);
		} else return new Command.Choice[0];
	}

	@Contract(pure = true, value = "_, _ -> new")
	private @NonNull Command.Choice[] autoCompletePage(@NonNull final String command, @NonNull final String input) {
		if (command.equals("hns list")) {
			return LongStream.range(1, Math.min(databaseAdapter.getHnsPages(), 25) + 1)
					.boxed()
					.filter(operand -> String.valueOf(operand + 1).startsWith(input))
					.map(page -> new Command.Choice(String.valueOf(page + 1), page))
					.toArray(Command.Choice[]::new);
		} else if (command.equals("pg list")) {
			return LongStream.range(1, Math.min(databaseAdapter.getPgPages(), 25) + 1)
					.boxed()
					.filter(operand -> String.valueOf(operand).startsWith(input))
					.map(page -> new Command.Choice(String.valueOf(page + 1), page))
					.toArray(Command.Choice[]::new);
		} else if (command.equals("tutorial")) {
			return Arrays.stream(R.Tutorials.list())
					.filter(tutorialModel -> tutorialModel.getTitle().contains(input) || tutorialModel.getId().contains(input))
					.limit(25)
					.map(tutorialModel -> new Command.Choice(tutorialModel.getTitle() + tutorialModel.getPrio(), tutorialModel.getId()))
					.toArray(Command.Choice[]::new);
		} else return new Command.Choice[0];
	}

	@Contract(pure = true, value = "_ -> new")
	private @NonNull Command.Choice[] autoCompleteTag(@NonNull final String input) {
		if (databaseAdapter.getGroups(input.toLowerCase(BuildConfig.locale)) instanceof List<GroupModel> groupModels) {
			return groupModels.stream()
					.limit(25)
					.map(groupModel -> new Command.Choice(groupModel.getName(), groupModel.getTag()))
					.toArray(Command.Choice[]::new);
		} else return new Command.Choice[0];
	}

	@Contract(pure = true, value = "_, _ -> new")
	private @NonNull Command.Choice[] autoCompleteLuck(@Nullable final OptionMapping winrateMapping, @Nullable final OptionMapping quotaMapping) {
		if (winrateMapping != null && quotaMapping != null) {
			return new Command.Choice[]{
					new Command.Choice(R.Strings.ui("calculated_value_for_s_s",
							R.Strings.ui("luck"),
							Calculator.calculateLuck(quotaMapping.getAsDouble(), winrateMapping.getAsDouble())),
							Calculator.calculateLuck(quotaMapping.getAsDouble(), winrateMapping.getAsDouble()))
			};
		} else return new Command.Choice[0];
	}
}
