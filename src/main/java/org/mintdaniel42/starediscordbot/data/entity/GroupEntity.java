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
@Table(name = "groups")
@Value
public class GroupEntity {
	@NonNull @Id String tag;
	@NonNull String name;
	@NonNull UUID leader;
	@NonNull Relation relation;

	public static @NonNull GroupEntity from(@NonNull final RequestEntity request) {
		return GroupEntity.builder()
				.tag(request.getTag())
				.name(request.getName())
				.leader(request.getLeader())
				.relation(request.getRelation())
				.build();
	}

	public enum Relation {
		ally,
		neutral,
		enemy
	}
}
