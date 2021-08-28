package com.backinfile.support;

public class Timer {
	private long interval = -1;
	private long timeout = -1;
	private Function<Long> getTimeFunc;

	public Timer(long delay) {
		this(-1, delay, null);
	}

	public Timer(long interval, long delay) {
		this(interval, delay, null);

	}

	public Timer(long interval, long delay, Function<Long> getTimeFunc) {
		this.interval = interval;
		this.timeout = Time2.currentTimeMillis() + delay;
		this.getTimeFunc = getTimeFunc;
	}

	public boolean isRunning() {
		return timeout >= 0;
	}

	public boolean isPeriod() {
		if (timeout < 0) {
			return false;
		}

		long time = getTimeFunc == null ? Time2.currentTimeMillis() : getTimeFunc.invoke();
		if (time >= timeout) {
			if (interval > 0) {
				timeout += interval;
			} else {
				timeout = -1;
			}
			return true;
		}
		return false;
	}
}
