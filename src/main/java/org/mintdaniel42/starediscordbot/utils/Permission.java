package org.mintdaniel42.starediscordbot.utils;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.BotConfig;

public enum Permission {
	p1,
	p2,
	p3,
	p4;

	public static @NonNull Permission fromUser(@NonNull final BotConfig config, @NonNull final Member member) {
		if (DCHelper.hasRole(member, config.getP4Id())) {
			return p4;
		} else if (DCHelper.hasRole(member, config.getP3Id())) {
			return p3;
		} else if (DCHelper.hasRole(member, config.getP2Id())) {
			return p2;
		} else return p1;
	}

	@Contract(pure = true)
	public static boolean hasP1() {
		return true;
	}

	@Contract("_, null -> false")
	public static boolean hasP2(@NonNull final BotConfig config, @Nullable final Member member) {
		return member != null && fromUser(config, member).ordinal() >= 1;
	}

	@Contract("_, null -> false")
	public static boolean hasP3(@NonNull final BotConfig config, @Nullable final Member member) {
		return member != null && fromUser(config, member).ordinal() >= 2;
	}

	@Contract("_, null -> false")
	public static boolean hasP4(@NonNull final BotConfig config, @Nullable final Member member) {
		return member != null && fromUser(config, member).ordinal() >= 3;
	}

	@Contract("null -> false")
	public static boolean hasAdmin(@Nullable final Member member) {
		if (member == null) return false;
		return member.hasPermission(net.dv8tion.jda.api.Permission.ADMINISTRATOR);
	}
}
