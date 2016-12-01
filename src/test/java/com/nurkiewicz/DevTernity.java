package com.nurkiewicz;

import com.nurkiewicz.cache.CacheServer;
import com.nurkiewicz.weather.Weather;
import com.nurkiewicz.weather.WeatherClient;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.rx.ReactiveCamel;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Tomasz Nurkiewicz
 *
 * nurkiewicz (at GMail)
 * @tnurkiewicz
 *
 * http://nurkiewicz.com
 * http://allegro.tech
 */
public class DevTernity {

	private static final Logger log = LoggerFactory.getLogger(DevTernity.class);

	@Test
	public void devternity_15() throws Exception {
		CompletableFuture<String> fs = CompletableFuture.completedFuture("42");
		//map()
		CompletableFuture<Integer> fi = fs.thenApply((String s) -> s.length() + 1);
	}

	@Test
	public void devternity_26() throws Exception {
		Observable<String> obs = Observable.just("4", "43", "445", "4567");

		final Observable<Integer> map = obs.map(s -> s.length());

		final Observable<Double> repeated = obs
				.map(s -> s.length())
				.filter(x -> (x <= 2))
				.map(x -> x * 2.0)
				.repeat(3);

		repeated.subscribe(dbl -> print(dbl));
	}

	void print(Object object) {
		log.info("Got: {}", object);
	}

	final WeatherClient weatherClient = new WeatherClient();

	@Test
	public void devternity_50() throws Exception {
		weatherClient.fetch("Riga");
	}

	@Test
	public void devternity_58() throws Exception {
		final Observable<Weather> riga = weatherClient.rxFetch("Riga");

		riga.subscribe(this::print);
		log.info("OK");
	}

	@Test
	public void devternity_67() throws Exception {
		final Observable<Weather> riga = weatherClient.rxFetch("Riga");

		riga
				.timeout(1, TimeUnit.SECONDS)
				.subscribe(this::print);
	}

	@Test
	public void devternity_77() throws Exception {
		final Observable<Weather> rig = weatherClient.rxFetch("Riga");
		final Observable<Weather> war = weatherClient.rxFetch("Warsaw");

		final Observable<Weather> both = rig.mergeWith(war);

		both.subscribe(this::print);
		TimeUnit.SECONDS.sleep(2);
	}

	@Test
	public void devternity_88() throws Exception {
		final CacheServer eu = new CacheServer();
		final CacheServer us = new CacheServer();

		final Observable<String> euObs = eu.rxFindBy(42);
		final Observable<String> usObs = us.rxFindBy(42);

		final Observable<String> result = Observable
				.merge(euObs, usObs)
				.first();
	}

	//CouchDB, MongoDB, RxNetty, RxAndroid, Retrofit
	@Test
	public void devternity_102() throws Exception {
		final DefaultCamelContext camel = new DefaultCamelContext();
		new ReactiveCamel(camel)
//				.toObservable("sftp:/home/tomek/tmp/devternity")
//				.toObservable("file:/home/tomek/tmp/devternity")
				.toObservable("activemq:queue:devternity")
//				.map(Message::getBody)
				.subscribe(this::print);

		TimeUnit.SECONDS.sleep(50000);
	}

	@Test
	public void devternity_119() throws Exception {
		final TestScheduler test = Schedulers.test();
		final Observable<BigDecimal> result = verySlowSoapService()
				.timeout(1, TimeUnit.SECONDS, test)
				.doOnError(er -> log.error("Error " + er))  //never do this at home
				.retry(4)
				.onErrorReturn(er -> BigDecimal.ONE.negate());

		//Awaitility
		//java.time.Clock

		final TestSubscriber<BigDecimal> subscriber = new TestSubscriber<>();
		result.subscribe(subscriber);

		test.advanceTimeBy(4_999, TimeUnit.MILLISECONDS);
		subscriber.assertNoValues();
		subscriber.assertNoErrors();

		test.advanceTimeBy(1, TimeUnit.MILLISECONDS);
		subscriber.assertNoErrors();
		subscriber.assertValues(BigDecimal.ONE.negate());

	}

	private Observable<BigDecimal> verySlowSoapService() {
		return Observable
				.just(BigDecimal.TEN)
				.delay(10, TimeUnit.MINUTES);
	}

	@Test
	public void devternity_154() throws Exception {
		Observable
				.interval(1, TimeUnit.SECONDS)
				.subscribe(this::print);

		TimeUnit.SECONDS.sleep(10);
	}

}

