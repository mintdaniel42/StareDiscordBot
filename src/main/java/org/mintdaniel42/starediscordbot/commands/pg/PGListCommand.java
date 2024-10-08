package org.mintdaniel42.starediscordbot.commands.pg;

import io.avaje.inject.RequiresBean;
import io.avaje.inject.RequiresProperty;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.buttons.list.PGListButtons;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.data.repository.PGUserRepository;
import org.mintdaniel42.starediscordbot.data.repository.ProfileRepository;
import org.mintdaniel42.starediscordbot.embeds.ListEmbed;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
@RequiresBean(PGCommand.class)
@RequiresProperty(value = "feature.command.pg.list.enabled", equalTo = "true")
@Singleton
public final class PGListCommand extends BaseComposeCommand {
	@NonNull private final PGUserRepository pgUserRepository;
	@NonNull private final ProfileRepository profileRepository;

	@Override
	protected @NonNull MessageEditData compose(@NonNull final CommandContext context) throws BotException {
		final var page = nullableIntegerOption(context, "page").orElse(1) - 1;
		final var pageCount = (int) Math.ceil((double) pgUserRepository.count() / BuildConfig.entriesPerPage);
		requireBounds(0, page, pageCount);
		var entries = pgUserRepository.selectAll(page * BuildConfig.entriesPerPage, BuildConfig.entriesPerPage);
		return response()
				.addEmbed(ListEmbed.createPgList(profileRepository, entries, page, pageCount))
				.addComponent(PGListButtons.create(page, pageCount))
				.compose();
	}

	@Inject
	public void register(@NonNull @Named("pg") SlashCommandData command) {
		command.addSubcommands(new SubcommandData("list", R.Strings.ui("list_partygames_entries"))
				.addOption(OptionType.INTEGER, "page", R.Strings.ui("page"), false, true));
	}

	@Override
	public @NonNull String getCommandId() {
		return "pg list";
	}
}
