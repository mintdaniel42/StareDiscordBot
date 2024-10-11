package org.mintdaniel42.starediscordbot.commands.hns.maps;

import io.avaje.inject.RequiresBean;
import io.avaje.inject.RequiresProperty;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.mintdaniel42.starediscordbot.aspect.annotation.NotYetImplemented;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.exception.BotException;

@RequiredArgsConstructor
@RequiresBean(MapsGroup.class)
@RequiresProperty(value = "feature.command.hns.maps.show.enabled", equalTo = "true")
@Singleton
public sealed class MapsShowCommand extends BaseComposeCommand permits MapsShowCommand$Proxy {
	@Override
	@NotYetImplemented
	protected @NonNull MessageEditData compose(@NonNull final CommandContext context) throws BotException {
		return null;
	}

	@Inject
	public void register(@NonNull @Named("hns maps") SubcommandGroupData group) {
		group.addSubcommands();
	}

	@Override
	public @NonNull String getCommandId() {
		return "hns maps show";
	}
}
