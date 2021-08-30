package com.backinfile.support;

import com.backinfile.map.Log;

public class TimeLogger {
	private long startTime;
	private String name;
	private long minTime;

	public TimeLogger(String name, long minTime) {
		this.name = name;
		this.minTime = minTime;
		startTime = Time2.currentTimeMillis();
	}

	public void log() {
		var passTime = Time2.currentTimeMillis() - startTime;
		if (minTime > 0 && passTime < minTime) {
			return;
		}
		Log.core.info("{} using {} second", name, passTime / (float) Time2.SEC);
	}
}
