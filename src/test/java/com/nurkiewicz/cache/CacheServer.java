package com.nurkiewicz.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheServer {

	private static final Logger log = LoggerFactory.getLogger(CacheServer.class);

	public String findBy(long key) {
		log.info("Loading from Memcached: {}", key);
		return "<data>" + key + "</data>";
	}

}
