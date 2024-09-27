package org.mintdaniel42.starediscordbot.bucket;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.mintdaniel42.starediscordbot.BotConfig;
import org.mintdaniel42.starediscordbot.utils.Permission;

import java.util.List;

public final class PoolUpdater extends ListenerAdapter {
	@NonNull private final PoolAdapter[] poolAdapters;
	@NonNull private final BotConfig config;

	public PoolUpdater(@NonNull final BotConfig config, @NonNull final PoolAdapter... poolAdapters) {
		this.poolAdapters = poolAdapters;
		this.config = config;
	}

	@Override
	public void onGuildMemberRoleAdd(@NonNull final GuildMemberRoleAddEvent event) {
		updateAll(event.getMember(), event.getRoles());
	}

	@Override
	public void onGuildMemberRoleRemove(@NonNull final GuildMemberRoleRemoveEvent event) {
		updateAll(event.getMember(), event.getRoles());
	}

	private void updateAll(@NonNull final Member member, @NonNull final List<Role> rolesDelta) {
		if (rolesDelta.stream()
				.map(Role::getIdLong)
				.anyMatch(id -> id.equals(config.getP2Id()) ||
						id.equals(config.getP3Id()) ||
						id.equals(config.getP4Id()))) {
			for (PoolAdapter poolAdapter : poolAdapters) {
				poolAdapter.onPermissionChanged(member, Permission.fromUser(member));
			}
		}
	}
}
