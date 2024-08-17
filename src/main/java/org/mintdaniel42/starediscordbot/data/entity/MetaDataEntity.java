package org.mintdaniel42.starediscordbot.data.entity;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

@Entity(immutable = true, metamodel = @Metamodel(suffix = "Meta"))
@Table(name = "metadata")
public record MetaDataEntity(@Id int id, @NonNull Version version) {
	@Getter
	@RequiredArgsConstructor
	public enum Version {
		UNKNOWN("?"),
		V1("Apollo"),
		V2("Ares"),
		V2_1("Artemis"),
		V2_2("Athena"),
		V2_3("Aphrodite"),
		V2_4("Demeter"),
		v3("Dionysus");

		@NonNull private final String title;
	}
}
