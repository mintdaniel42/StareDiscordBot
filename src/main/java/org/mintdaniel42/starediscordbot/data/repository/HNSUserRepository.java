package org.mintdaniel42.starediscordbot.data.repository;

import jakarta.inject.Singleton;
import lombok.NonNull;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.data.entity.HNSUserEntity;
import org.mintdaniel42.starediscordbot.data.entity.HNSUserEntityMeta;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

import java.util.List;
import java.util.UUID;

@Singleton
public final class HNSUserRepository extends BaseRepository<UUID, HNSUserEntity> {
	public HNSUserRepository(@NonNull final Config config) {
		final var meta = new HNSUserEntityMeta();
		super(new Entityql(config), meta, meta.uuid);
	}

	public @NonNull List<HNSUserEntity> selectByPage(final int page) {
		return entityQl.from(meta)
				.orderBy(o -> o.desc(((HNSUserEntityMeta) meta).points))
				.stream()
				.skip((long) BuildConfig.entriesPerPage * page)
				.limit(BuildConfig.entriesPerPage)
				.toList();
	}

	public int countPages() {
		return (int) Math.ceil((double) entityQl.from(meta)
				.fetch()
				.size() / BuildConfig.entriesPerPage);
	}
}
