package org.mintdaniel42.starediscordbot.commands.hns.maps;

import io.avaje.inject.RequiresBean;
import io.avaje.inject.RequiresProperty;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.BotConfig;
import org.mintdaniel42.starediscordbot.aspect.annotation.NotYetImplemented;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.data.entity.MapEntity;
import org.mintdaniel42.starediscordbot.data.repository.MapRepository;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.Permission;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.UUID;

@RequiredArgsConstructor
@RequiresBean(MapsGroup.class)
@RequiresProperty(value = "feature.command.hns.maps.add.enabled", equalTo = "true")
@Singleton
public sealed class MapsAddCommand extends BaseComposeCommand permits MapsAddCommand$Proxy {
	@NonNull private final MapRepository mapRepository;

	@Override
	@NotYetImplemented
	public @NonNull MessageEditData compose(@NonNull final CommandContext context) throws BotException {
		final var builder = MapEntity.builder().uuid(UUID.randomUUID());
		requireStringOption(context, "name", builder::name);
		//final var picture = requireAttachmentOption(context, "picture");
		//requireStringOption(context, "blocks", builder::blocks)
		final var map = builder.build();
		return fail("this_is_not_yet_implemented");
	}

	@Override
	public @NonNull String getCommandId() {
		return "hns maps add";
	}

	@Override
	public boolean hasPermission(@NonNull final BotConfig config, @Nullable final Member member) {
		return Permission.hasP4(config, member);
	}

	@Inject
	public void register(@NonNull @Named("hns maps") SubcommandGroupData group) {
		group.addSubcommands(new SubcommandData("add", R.Strings.ui("add_a_map"))
				.addOption(OptionType.STRING, "name", R.Strings.ui("the_maps_name"), true)
				.addOption(OptionType.STRING, "builder", R.Strings.ui("the_builders_names"), true, true)
				.addOption(OptionType.STRING, "release", R.Strings.ui("the_maps_release_date"), true)
				.addOption(OptionType.ATTACHMENT, "picture", R.Strings.ui("a_picture_of_the_map"), true)
				.addOptions(new OptionData(OptionType.STRING, "difficulty", R.Strings.ui("the_maps_difficulty"), true)
						.addChoice(R.Strings.ui("easy"), MapEntity.Difficulty.easy.name())
						.addChoice(R.Strings.ui("medium"), MapEntity.Difficulty.medium.name())
						.addChoice(R.Strings.ui("hard"), MapEntity.Difficulty.hard.name())
						.addChoice(R.Strings.ui("ultra"), MapEntity.Difficulty.ultra.name())
				)
		);
	}
}
