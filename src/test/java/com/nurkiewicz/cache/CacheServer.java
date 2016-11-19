package com.nurkiewicz.cache;

import com.nurkiewicz.util.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class CacheServer {

	private static final Logger log = LoggerFactory.getLogger(CacheServer.class);

	public String findBy(long key) {
		log.info("Loading from Memcached: {}", key);
		Sleeper.sleep(Duration.ofMillis(10));
		return "<data>" + key + "</data>";
	}

	public Observable<String> rxFindBy(long key) {
		return Observable
				.fromCallable(() -> findBy(key))
				.subscribeOn(Schedulers.io())
				.timeout(50, TimeUnit.MILLISECONDS);
	}
}
