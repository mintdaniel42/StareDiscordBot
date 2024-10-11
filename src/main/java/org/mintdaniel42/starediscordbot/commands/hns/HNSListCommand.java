package org.mintdaniel42.starediscordbot.commands.hns;

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
import org.mintdaniel42.starediscordbot.buttons.list.HNSListButtons;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.data.repository.HNSUserRepository;
import org.mintdaniel42.starediscordbot.data.repository.ProfileRepository;
import org.mintdaniel42.starediscordbot.embeds.ListEmbed;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
@RequiresBean(HNSCommand.class)
@RequiresProperty(value = "feature.command.hns.list.enabled", equalTo = "true")
@Singleton
public final class HNSListCommand extends BaseComposeCommand {
	@NonNull private final HNSUserRepository hnsUserRepository;
	@NonNull private final ProfileRepository profileRepository;

	@Override
	protected @NonNull MessageEditData compose(@NonNull final CommandContext context) throws BotException {
		final int page = nullableIntegerOption(context, "page").orElse(1) - 1;
		final var pageCount = (int) Math.ceil((double) hnsUserRepository.count() / BuildConfig.entriesPerPage);
		requireBounds(0, page, pageCount);
		final var entries = hnsUserRepository.selectAll(page * BuildConfig.entriesPerPage, BuildConfig.entriesPerPage);
		return response()
				.addEmbed(ListEmbed.createHnsList(profileRepository, entries, page, pageCount))
				.addComponent(HNSListButtons.create(page, pageCount))
				.compose();
	}

	@Inject
	public void register(@NonNull @Named("hns") SlashCommandData command) {
		command.addSubcommands(new SubcommandData("list", R.Strings.ui("list_hide_n_seek_entries"))
				.addOption(OptionType.INTEGER, "page", R.Strings.ui("page"), false, true));
	}

	@Override
	public @NonNull String getCommandId() {
		return "hns list";
	}
}
