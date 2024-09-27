package org.mintdaniel42.starediscordbot.di;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import okhttp3.OkHttpClient;

@Factory
public final class OkHttpClientFactory {
	@Bean
	public OkHttpClient build() {
		return new OkHttpClient();
	}
}
