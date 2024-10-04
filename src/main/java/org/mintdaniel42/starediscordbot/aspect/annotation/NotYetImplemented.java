package org.mintdaniel42.starediscordbot.aspect.annotation;

import io.avaje.inject.aop.Aspect;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.exception.NotYetImplementedException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation on any method that is not yet ready to be shipped
 */
@Aspect
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotYetImplemented {
	/**
	 * The flag / condition under which the call to this method should be blocked and just throw an
	 * {@link NotYetImplementedException}
	 *
	 * @return whether it should be blocked or not
	 */
	boolean value() default BuildConfig.production;
}
