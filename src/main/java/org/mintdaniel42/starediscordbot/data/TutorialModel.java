package org.mintdaniel42.starediscordbot.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor(force = true)
@Value
public class TutorialModel {
	@JsonIgnore String id;
	@NonNull String title;
	@NonNull String description;
	int color;
	@Nullable String thumbnailUrl;
	@Nullable String imageUrl;
	@NonNull String[] similar;
}
