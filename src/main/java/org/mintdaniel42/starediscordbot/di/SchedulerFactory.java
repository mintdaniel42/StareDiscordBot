package org.mintdaniel42.starediscordbot.di;

import com.coreoz.wisp.Scheduler;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;

@Factory
public class SchedulerFactory {
	@Bean
	public Scheduler build() {
		return new Scheduler();
	}
}
