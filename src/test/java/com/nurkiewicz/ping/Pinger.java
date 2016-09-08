package com.nurkiewicz.ping;

import static com.nurkiewicz.ping.Status.DOWN;
import static com.nurkiewicz.ping.Status.UP;

public class Pinger {

	public Status healthy() {
		return Math.random() < 0.9 ? UP : DOWN;
	}

}

