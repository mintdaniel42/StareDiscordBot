package org.mintdaniel42.starediscordbot.utils;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class Permissions {
	@Contract(pure = true)
	public boolean view() {
		return !Options.isInMaintenance();
	}

	@Contract("null -> false")
	public boolean edit(@Nullable final Member member) {
		if (Options.isInMaintenance()) return false;
		return DCHelper.hasRole(member, Options.getEditRoleId()) || DCHelper.hasRole(member, Options.getCreateRoleId());
	}

	@Contract("null -> false")
	public boolean create(@Nullable final Member member) {
		if (Options.isInMaintenance()) return false;
		return DCHelper.hasRole(member, Options.getCreateRoleId());
	}

	@Contract("null -> false")
	public boolean manage(@Nullable final Member member) {
		if (member == null) return false;
		return member.hasPermission(Permission.ADMINISTRATOR);
	}
}
