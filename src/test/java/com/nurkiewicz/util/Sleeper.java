package com.nurkiewicz.util;

import com.google.common.base.Throwables;

import java.time.Duration;

public class Sleeper {

	public static void sleep(Duration duration) {
		try {
			Thread.sleep(duration.toMillis());
		} catch (InterruptedException e) {
			throw Throwables.propagate(e);
		}
	}

}
