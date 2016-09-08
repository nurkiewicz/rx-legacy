package com.nurkiewicz.dao;

import com.nurkiewicz.util.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.time.Duration;

public class PersonDao {

	private static final Logger log = LoggerFactory.getLogger(PersonDao.class);

	public Person findById(int id) {
		//SQL, SQL, SQL
		log.info("Loading {}", id);
		Sleeper.sleep(Duration.ofMillis(1000));
		return new Person();
	}

	public Observable<Person> rxFindById(int id) {
		return Observable
				.fromCallable(() -> findById(id))
				.subscribeOn(Schedulers.io());
	}

}
