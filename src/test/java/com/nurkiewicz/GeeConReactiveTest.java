package com.nurkiewicz;

import com.nurkiewicz.cache.CacheServer;
import com.nurkiewicz.dao.Person;
import com.nurkiewicz.dao.PersonDao;
import com.nurkiewicz.weather.Weather;
import com.nurkiewicz.weather.WeatherClient;
import org.apache.camel.CamelContext;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.rx.ReactiveCamel;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.observables.BlockingObservable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @tnurkiewicz
 * nurkiewicz@gmail.com
 * nurkiewicz.com
 */
@Ignore
public class GeeConReactiveTest {

	private static final Logger log = LoggerFactory.getLogger(GeeConReactiveTest.class);
	public static final long FAILURE = -123L;

	@Test
	public void geeconReactive_14() {
		rx.Observable<String> thing = Observable.just("a", "b", "c");

//		thing.map()
//		thing.filter()
//		thing.flatMap()


		thing.subscribe(
				(String str) -> System.out.println(str),
				ex -> log.error("Opps", ex)
		);
	}

	PersonDao dao = new PersonDao();

	@Test
	public void geeconReactive_35() {
		Person byId = dao.findById(42);
		System.out.println("byId = " + byId);
	}

	@Test
	public void geeconReactive_45() {
		Observable<Person> obs = dao
				.rxFindById(42)
				.timeout(100, TimeUnit.MILLISECONDS);
		obs.subscribe(
				this::print,
				ex -> log.error("Not expected?", ex)
				);
	}



	void print(Object obj) {
		log.info("Got: {}", obj);
	}

	WeatherClient weatherClient = new WeatherClient();

	@Test
	public void geeconReactive_64() {
		Observable<Person> person = dao.rxFindById(42);
		Observable<Weather> sopot = weatherClient.rxFetch("Sopot");

		Observable<String> str = person.zipWith(sopot,
				(Person p, Weather w) -> p + ":" + w);

		BlockingObservable<String> blocking = str
				.toBlocking();
		blocking
				.subscribe(this::print);
	}

	@Test
	public void geeconReactive_83() {
		CacheServer eu = new CacheServer();
		CacheServer us = new CacheServer();

		Observable<String> euResp = eu.rxFindBy(42);
		Observable<String> usResp = us.rxFindBy(42);

		Observable<String> allResponses = euResp
				.mergeWith(usResp)
				.first();

		allResponses
				.toBlocking()
				.subscribe(this::print);
	}

	@Test
	public void geeconReactive_101() {
		File file = new File("/home/tomek/geecon");
		Observable
				.interval(1, TimeUnit.SECONDS)
				.concatMapIterable(x -> childrenOf(file))
				.distinct(/*cacheWithExpiry()*/)
//				.distinctUntilChanged()
				.toBlocking()
				.subscribe(this::print);
	}

	List<String> childrenOf(File parent) {
		return Arrays
				.asList(parent.listFiles())
				.stream()
				.map(File::getName)
				.collect(Collectors.toList());
	}

	@Test
	public void geeconReactive_126() {
		TestScheduler clock = Schedulers.test();
		Observable<Long> soap = typicalSoapService()
				.timeout(2, TimeUnit.SECONDS, clock)
				.doOnError(ex -> log.warn("Failed: " + ex))  //intentionally
				.retry(4)
				.onErrorReturn(ex -> FAILURE);


		TestSubscriber<Long> subscriber = new TestSubscriber<>();
		soap.subscribe(subscriber);

		subscriber.assertNoValues();
		subscriber.assertNoErrors();

		clock.advanceTimeBy(9999, TimeUnit.MILLISECONDS);
		subscriber.assertNoValues();
		subscriber.assertNoErrors();

		clock.advanceTimeBy(1, TimeUnit.MILLISECONDS);

		subscriber.assertNoErrors();
		subscriber.assertValues(FAILURE);
	}

	//Fake clock pattern
	//Awaitility - workaround

	Observable<Long> typicalSoapService() {
		return Observable
				.timer(10, TimeUnit.MINUTES);
	}

	@Test
	public void geeconReactive_164() {
		CamelContext context = new DefaultCamelContext();
		Observable<Message> msg = new ReactiveCamel(context)
				.toObservable("file:/home/tomek/geecon");

		msg
				.map(m -> m.getBody())
				.toBlocking()
				.subscribe(this::print);
	}

	@Test
	public void geeconReactive_180() {
		CamelContext context = new DefaultCamelContext();
		Observable<Message> msg = new ReactiveCamel(context)
				.toObservable("activemq:queue:geecon");

		msg
				.map(m -> m.getBody())
				.toBlocking()
				.subscribe(this::print);

	}

}
