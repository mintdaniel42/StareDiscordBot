package org.mintdaniel42.starediscordbot.plugin;

import io.avaje.config.Config;
import io.avaje.inject.BeanScopeBuilder;
import io.avaje.inject.spi.InjectPlugin;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.mintdaniel42.starediscordbot.commands.CommandAdapter;
import org.mintdaniel42.starediscordbot.data.repository.BaseRepository;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.ServiceLoader;

@Slf4j
public final class PluginLoader implements InjectPlugin {
	@Override
	public void apply(@NonNull final BeanScopeBuilder builder) {
		for (final var plugin : ServiceLoader.load(Plugin.class)) {
			if (Config.enabled("plugin." + plugin.getPluginId() + ".enabled", true)) {
				log.info(R.Strings.log("loading_plugin_s", plugin.getPluginId()));
				builder.bean(Plugin.class, plugin);
				if (plugin instanceof CommandAdapter commandAdapter) {
					builder.bean(CommandAdapter.class, commandAdapter);
				}
				if (plugin instanceof BaseRepository<?, ?> baseRepository) {
					builder.bean(BaseRepository.class, baseRepository);
				}
			}
		}
	}

	@Override
	public @NonNull Class<?>[] provides() {
		return new Class<?>[]{
				CommandAdapter.class,
				BaseRepository.class,
				Plugin.class
		};
	}
}
