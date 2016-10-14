package com.nurkiewicz;

import com.nurkiewicz.cache.CacheServer;
import com.nurkiewicz.dao.Person;
import com.nurkiewicz.dao.PersonDao;
import com.nurkiewicz.weather.Weather;
import com.nurkiewicz.weather.WeatherClient;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.rx.ReactiveCamel;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
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
public class JugtoberfestTest {
	
	private static final Logger log = LoggerFactory.getLogger(JugtoberfestTest.class);
	private final PersonDao personDao = new PersonDao();
	
	private WeatherClient weatherClient = new WeatherClient();
	
	
	@Test
	public void jugtoberFest_13() {
		CompletableFuture<String> f = CompletableFuture
				.completedFuture("abc");
		
		CompletableFuture<Integer> f2 = f.thenApply(str -> str.length());
	}
	
	@Test
	public void jugtoberFest_24() {
		Observable<Integer> obs = Observable.just(1, 2, 3);
		
		Observable<String> obs2 = obs.map((Integer x) -> "" + x);
	}
	
	@Test
	public void jugtoberFest_32() {
//		Weather poznań = weatherClient.fetch("Poznań");
		Observable<Weather> poz = weatherClient.rxFetch("Poznań");
		poz.subscribe(
				this::print
		);
		
	}
	
	public void print(Object obj) {
		log.info("Got: {}", obj);
	}
	
	@Test
	public void jugtoberFest_55() {
		weatherClient
				.rxFetch("Poznań")
				.timeout(1, TimeUnit.SECONDS)
				.subscribe(this::print);
	}
	
	@Test
	public void jugtoberFest_64() {
		Observable<Person> personObs = personDao.rxFindById(42);
		Observable<Weather> poz = weatherClient.rxFetch("Poznań");
		
		personObs
				.zipWith(poz, (Person p, Weather w) -> p + " : " + w)
				.toBlocking()
				.subscribe(this::print);
	}
	
	@Test
	public void jugtoberFest_77() {
		CacheServer c1 = new CacheServer();
		CacheServer c2 = new CacheServer();
		
		Observable<String> r1 = c1.rxFindBy(42)
				.timeout(100, TimeUnit.MILLISECONDS)
				.onErrorResumeNext(ex -> Observable.empty());
		Observable<String> r2 = c2.rxFindBy(42)
				.timeout(100, TimeUnit.MILLISECONDS)
				.onErrorResumeNext(ex -> Observable.empty());
		
		Observable<String> result = r1
				.mergeWith(r2)
				.concatWith(Observable.just("Nie udało się"))
				.first();
		
		result.toBlocking().subscribe(this::print);
	}
	
	@Test
	public void jugtoberFest_107() {
		Observable
				.empty()
				.first()
				.subscribe();
	}
	
	@Test
	public void jugtoberFest_95() {
		Observable
				.interval(1, TimeUnit.SECONDS)
				.toBlocking()
				.subscribe(this::print);
	}
	
	@Test
	public void jugtoberFest_103() {
		System.out.println(
				childrenOf(
						new File("/home/tomek/tmp/jugtoberfest")));
	}
	
	public List<String> childrenOf(File file) {
		return Arrays
				.asList(file.listFiles())
				.stream()
				.map(File::getName)
				.collect(Collectors.toList());
	}
	
	@Test
	public void jugtoberFest_122() {
		File file = new File("/home/tomek/tmp/jugtoberfest");
		Observable
				.interval(1, TimeUnit.SECONDS)
				.flatMap(x -> Observable.from(childrenOf(file)))
//				.concatMapIterable(x -> childrenOf(file))
				.distinct()
//				.distinctUntilChanged()  //tutaj nie ma sensu
				.toBlocking()
				.subscribe(this::print);
	}

	// Awaitility - generalnie jest super
	// Fake system clock
	// java.time.Clock
	@Test
	public void jugtoberFest_135() {
		TestScheduler testSched = Schedulers.test();
		TestSubscriber<String> subscriber = new TestSubscriber<>();
		verySlowSoapService()
				.timeout(1, TimeUnit.SECONDS, testSched)
				.doOnError(ex -> log.warn("Opps: " + ex))  //nigdy nie róbcie tak w domu
				.retry(4)
				.onErrorReturn(ex -> "<error/>")
				.subscribe(subscriber);

		//GC
		subscriber.assertNoValues();
		subscriber.assertNoErrors();
		
		testSched.advanceTimeBy(4999, TimeUnit.MILLISECONDS);
		
		subscriber.assertNoValues();
		subscriber.assertNoErrors();

		testSched.advanceTimeBy(1, TimeUnit.MILLISECONDS);

		subscriber.assertValue("<error/>");
		subscriber.assertNoErrors();
	}
	
	private Observable<String> verySlowSoapService() {
		return Observable
				.timer(10, TimeUnit.MINUTES)
				.map(x -> "<data/>");
	}
	
	@Test
	public void jugtoberFest_173() {
		DefaultCamelContext camelContext = new DefaultCamelContext();
		ReactiveCamel camel = new ReactiveCamel(camelContext);
		
		camel
				.toObservable("file:/home/tomek/tmp/jugtoberfest")
				.map(Message::getBody)
				.toBlocking()
				.subscribe(this::print);
				
	}
	
	@Test
	public void jugtoberFest_174() {
		DefaultCamelContext camelContext = new DefaultCamelContext();
		new ReactiveCamel(camelContext)
				.toObservable("activemq:queue:sopot")
				.map(Message::getBody)
				.toBlocking()
				.subscribe(this::print);
	}
	
}
