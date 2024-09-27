package org.mintdaniel42.starediscordbot.commands.hns.maps;

import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.aspect.annotation.NotYetImplemented;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.compose.exception.ComposeException;
import org.mintdaniel42.starediscordbot.data.entity.MapEntity;
import org.mintdaniel42.starediscordbot.data.repository.MapRepository;
import org.mintdaniel42.starediscordbot.utils.Permission;

import java.util.UUID;

@RequiredArgsConstructor
@Singleton
public class MapsAddCommand extends BaseComposeCommand {
	@NonNull private final MapRepository mapRepository;

	@Override
	@NotYetImplemented
	public @NonNull MessageEditData compose(@NonNull final CommandContext context) throws ComposeException {
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
	public boolean hasPermission(@Nullable final Member member) {
		return Permission.hasP4(member);
	}
}
