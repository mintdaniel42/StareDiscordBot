package org.mintdaniel42.starediscordbot.aspect.interceptor;

import io.avaje.inject.aop.AspectProvider;
import io.avaje.inject.aop.Invocation;
import io.avaje.inject.aop.MethodInterceptor;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.mintdaniel42.starediscordbot.aspect.annotation.NotYetImplemented;
import org.mintdaniel42.starediscordbot.exception.NotYetImplementedException;

import java.lang.reflect.Method;

@Singleton
public final class NotYetImplementedInterceptorProvider implements AspectProvider<NotYetImplemented> {
	@Override
	public MethodInterceptor interceptor(Method method, NotYetImplemented annotation) {
		return new NotYetImplementedInterceptor(annotation);
	}

	@RequiredArgsConstructor
	static class NotYetImplementedInterceptor implements MethodInterceptor {
		@NonNull private final NotYetImplemented annotation;

		@Override
		public void invoke(Invocation invocation) throws Throwable {
			if (annotation.value()) throw new NotYetImplementedException();
			else invocation.invoke();
		}
	}
}
