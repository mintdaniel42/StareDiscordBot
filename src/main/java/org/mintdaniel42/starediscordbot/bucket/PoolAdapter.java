package org.mintdaniel42.starediscordbot.bucket;

import io.github.bucket4j.Bucket;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Member;
import org.mintdaniel42.starediscordbot.utils.Permission;

public interface PoolAdapter {
	/**
	 * This is called whenever a discord users' permission level changes
	 *
	 * @param member     the discord user
	 * @param permission the new permission level
	 */
	void onPermissionChanged(@NonNull final Member member, @NonNull final Permission permission);

	/**
	 * @param member the discord user
	 * @return bucket for the specified user
	 */
	@NonNull
	Bucket getBucket(@NonNull final Member member);
}
