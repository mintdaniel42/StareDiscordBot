package org.mintdaniel42.starediscordbot.data.entity;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

import java.util.UUID;

@Builder
@Entity(immutable = true, metamodel = @Metamodel(suffix = "Meta"))
@Table(name = "profiles")
@Value
public class ProfileEntity {
	@NonNull @Id UUID uuid;
	@NonNull String username;
	long lastUpdated;
}
