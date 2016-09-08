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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @tnurkiewicz
 * nurkiewicz@gmail.com
 */
@Ignore
public class JavaZoneTest {

	private PersonDao dao = new PersonDao();
	private WeatherClient weatherClient = new WeatherClient();

	@Test
	public void javazone_6() {
		CompletableFuture<String> future =
				CompletableFuture.completedFuture("abc");

		CompletableFuture<Integer> intFuture =
				future.thenApply((String s) -> s.length());
	}

	private static final Logger log = LoggerFactory.getLogger(JavaZoneTest.class);

	@Test
	public void javazone_20() {
		Observable<String> obs = Observable.just("A", "B", "C");

		Observable<Integer> ints = obs.map((String s) -> s.length());

		ints.subscribe(
				(Integer i) -> System.out.println(i),
				ex -> log.error("Opps", ex)
		);
	}

	@Test
	public void javazone_40() {

		Observable<Person> obs = dao.rxFindById(42);

		Observable<String> map = obs.map(Person::toString);

		map.subscribe(this::print);
	}

	@Test
	public void javazone_55() {
		Observable<Person> obs = dao
				.rxFindById(42)
				.timeout(1100, TimeUnit.MILLISECONDS);

		obs.subscribe(this::print);
	}

	@Test
	public void javazone_64() {
		Observable<Person> person = dao.rxFindById(42);
		Observable<Weather> oslo = weatherClient.rxFetch("Oslo");

		Observable<String> str = person.zipWith(oslo,
				(Person p, Weather w) -> p + ":" + w);

		BlockingObservable<String> block = str.toBlocking();


		block.subscribe(this::print);
	}

	public void print(Object obj) {
		log.info("Got: {}", obj);
	}

	@Test
	public void javazone_86() {
		CacheServer c1 = new CacheServer();
		CacheServer c2 = new CacheServer();

		Observable<String> s1 = c1.rxFindBy(123);
		Observable<String> s2 = c2.rxFindBy(123);

		Observable<String> allResults = s1
				.mergeWith(s2)
				.first();

		allResults
				.toBlocking()
				.subscribe(this::print);
	}

	@Test
	public void javazone_104() {
		Observable
				.interval(1, TimeUnit.SECONDS)
				.toBlocking()
				.subscribe(this::print);
	}

	@Test
	public void javazone_118() {
		File tmp = new File("/home/tomek/tmp/javazone2");
		System.out.println(childrenOf(tmp));
	}

	@Test
	public void javazone_124() {
		File tmp = new File("/home/tomek/tmp/javazone2");
		Observable
				.interval(1, TimeUnit.SECONDS)
				.concatMapIterable(x -> childrenOf(tmp))
				.distinct()
				.toBlocking()
				.subscribe(this::print);
	}

	List<String> childrenOf(File file) {
		return Arrays
				.asList(file.listFiles())
				.stream()
				.map(File::getName)
				.collect(Collectors.toList());
	}

	//Awaitility - workaround
	//Fake system clock pattern

	@Test
	public void javazone_145() {
		Observable<Long> soap = verySlowSoapService();

		TestScheduler testSched = Schedulers.test();
		TestSubscriber<Long> testSub = new TestSubscriber<>();
		soap
				.timeout(2, TimeUnit.SECONDS, testSched)
				.doOnError(ex -> log.warn("Opps " + ex))
				.retry(4)
				.onErrorReturn(ex -> -1L)
				.subscribe(testSub);

		testSub.assertNoValues();
		testSub.assertNoErrors();

		testSched.advanceTimeBy(9999, TimeUnit.MILLISECONDS);

		testSub.assertNoValues();
		testSub.assertNoErrors();

		testSched.advanceTimeBy(1, TimeUnit.MILLISECONDS);

		testSub.assertNoErrors();
		testSub.assertValue(-1L);

	}

	@Test
	public void javazone_179() {
		CamelContext camel = new DefaultCamelContext();
		Observable<Message> files = new ReactiveCamel(camel)
				.toObservable("file:/home/tomek/tmp/javazone2");

		files
				.map(Message::getBody)
				.toBlocking()
				.subscribe(this::print);
	}

	@Test
	public void javazone_195() {
		CamelContext camel = new DefaultCamelContext();
		new ReactiveCamel(camel)
				.toObservable("activemq:queue:javazone")
				.map(m -> m.getBody())
				.toBlocking()
				.subscribe(this::print);
	}

	Observable<Long> verySlowSoapService() {
		return Observable.timer(10, TimeUnit.MINUTES);
	}
}
