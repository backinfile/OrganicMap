package com.backinfile.support;

public class Time2 {
	public static final long SEC = 1000;
	public static final long MIN = 60 * SEC;
	public static final long HOUR = 60 * MIN;
	public static final long DAY = 24 * HOUR;
	public static final long MONTH = 30 * DAY;
	public static final long YEAR = 12 * MONTH;

	public static long currentTimeMillis() {
		return System.currentTimeMillis();
	}

	public static long getCurMillis() {
		return currentTimeMillis();
	}
}
