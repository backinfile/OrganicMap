package com.backinfile.support;

@FunctionalInterface
public interface Function1<R, T> {
	R invoke(T t);
}
