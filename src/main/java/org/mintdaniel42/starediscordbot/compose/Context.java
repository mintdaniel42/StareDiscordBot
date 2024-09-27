package org.mintdaniel42.starediscordbot.compose;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.managers.Presence;

import java.util.Optional;

@RequiredArgsConstructor
public abstract class Context {
	@NonNull private final JDA jda;
	@Getter @NonNull private final Member member;

	public @NonNull Optional<Guild> getGuild(final long id) {
		return Optional.ofNullable(jda.getGuildById(id));
	}

	public @NonNull Presence getPresence() {
		return jda.getPresence();
	}
}
