package org.mintdaniel42.starediscordbot.data.entity;

import io.avaje.jsonb.Json;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.utils.Options;

@Builder(toBuilder = true)
@Value
@Json
public class TutorialEntity implements Comparable<TutorialEntity> {
	@Builder.Default int priority = Integer.MAX_VALUE;
	@Json.Ignore(deserialize = true) String id;
	@NonNull String title;
	@Nullable String summary;
	@NonNull String description;
	@Builder.Default int color = Options.getColorNormal();
	@Nullable String thumbnailUrl;
	@Nullable String imageUrl;
	@NonNull String[] similar;

	@Override
	public int compareTo(@NonNull final TutorialEntity other) {
		return Integer.compare(priority, other.getPriority());
	}
}
