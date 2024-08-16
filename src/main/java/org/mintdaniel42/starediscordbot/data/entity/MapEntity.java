package org.mintdaniel42.starediscordbot.data.entity;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

import java.util.Date;
import java.util.UUID;

@Builder
@Entity(immutable = true, metamodel = @Metamodel(suffix = "Meta"))
@Table(name = "maps")
@Value
public class MapEntity {
	@NonNull @Id UUID uuid;
	@NonNull String name;
	// TODO
	//@NonNull UUID[] builders;
	@NonNull Date release;
	// TODO
	//@NonNull String[] blocks;
	@NonNull Object fastestMatch; // TODO: records required, placeholder only
	@NonNull Difficulty difficulty;
	// TODO get picture method

	public enum Difficulty {
		easy,
		medium,
		hard,
		ultra
	}
}
