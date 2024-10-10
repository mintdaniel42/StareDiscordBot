package org.mintdaniel42.starediscordbot;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Version {
	UNKNOWN("?"),
	V2_3("Aphrodite"),
	V2_4("Demeter"),
	V3("Dionysus");

	@NonNull private final String title;
}
