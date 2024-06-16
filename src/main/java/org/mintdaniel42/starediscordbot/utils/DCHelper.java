package org.mintdaniel42.starediscordbot.utils;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class DCHelper {
    @Contract(pure = true, value = "null, _ -> false")
    public boolean hasRole(@Nullable final Member member, final long roleId) {
        return member != null && member.getRoles()
                .stream()
                .anyMatch(role -> role.getIdLong() == roleId);
    }
}
