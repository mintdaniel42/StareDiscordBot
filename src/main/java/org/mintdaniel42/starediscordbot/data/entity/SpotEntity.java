package org.mintdaniel42.starediscordbot.data.entity;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

import java.util.UUID;

@Builder(toBuilder = true)
@Entity(immutable = true, metamodel = @Metamodel(suffix = "Meta"))
@Table(name = "spots")
@Value
public class SpotEntity {
	@NonNull @Id UUID uuid;
	@NonNull UUID mapUUID;
	UUID finderUUID;
	@NonNull String blockId;
	@NonNull String rating;
	@NonNull String videolink;
	@NonNull Type type;
	boolean twoPlayers;

	public enum Type {
		afk,
		spelling
	}
}
