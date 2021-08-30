package com.backinfile.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Timing {
	String value() default "";

	/**
	 * 时长超过此时才打印
	 */
	long minTime() default -1;
}
