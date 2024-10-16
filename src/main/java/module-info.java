import com.codahale.metrics.MetricRegistry;
import io.avaje.inject.Component;
import io.avaje.inject.spi.InjectExtension;
import io.avaje.inject.spi.InjectPlugin;
import io.avaje.jsonb.spi.JsonbExtension;
import org.mintdaniel42.starediscordbot.plugin.PluginLoader;
import org.seasar.doma.jdbc.criteria.Entityql;

@Component.Import({Entityql.class, MetricRegistry.class})
module starediscordbot.core {
	uses org.mintdaniel42.starediscordbot.plugin.Plugin;
	opens org.mintdaniel42.starediscordbot.compose;
	opens org.mintdaniel42.starediscordbot.plugin;
	provides InjectExtension with org.mintdaniel42.starediscordbot.StarediscordbotModule;
	provides InjectPlugin with PluginLoader;
	provides JsonbExtension with org.mintdaniel42.starediscordbot.data.entity.jsonb.GeneratedJsonComponent;
	requires static lombok;
	requires static kotlin.stdlib;
	requires static org.jetbrains.annotations;
	requires static jakarta.inject;
	requires io.avaje.jsonb.plugin;
	requires io.avaje.inject;
	requires io.avaje.jsonb;
	requires io.avaje.config;
	requires io.github.bucket4j.core;
	requires java.management;
	requires org.slf4j;
	requires net.dv8tion.jda;
	requires okhttp3;
	requires org.apache.commons.io;
	requires org.json;
	requires org.seasar.doma.core;
	requires com.codahale.metrics;
	requires io.avaje.inject.aop;
	requires java.naming;
	requires com.coreoz.wisp;
}