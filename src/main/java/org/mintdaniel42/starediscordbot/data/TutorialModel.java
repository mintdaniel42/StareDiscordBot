package org.mintdaniel42.starediscordbot.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.utils.Options;

@AllArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor(force = true)
@Value
public class TutorialModel implements Comparable<TutorialModel> {
	@Builder.Default @JsonInclude int prio = Integer.MAX_VALUE;
	@JsonIgnore String id;
	@NonNull String title;
	@NonNull String description;
	int color = Options.getColorNormal();
	@Nullable String thumbnailUrl;
	@Nullable String imageUrl;
	@NonNull String[] similar;

	@Override
	public int compareTo(@NonNull final TutorialModel other) {
		return Integer.compare(prio, other.getPrio());
	}
}
