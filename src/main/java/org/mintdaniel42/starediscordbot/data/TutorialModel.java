package org.mintdaniel42.starediscordbot.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.utils.Options;

@AllArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor(force = true)
@Value
public class TutorialModel {
	@JsonIgnore String id;
	@NonNull String title;
	@NonNull String description;
	int color = Options.getColorNormal();
	@Nullable String thumbnailUrl;
	@Nullable String imageUrl;
	@NonNull String[] similar;
}
