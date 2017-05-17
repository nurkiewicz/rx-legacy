package com.nurkiewicz;


import com.nurkiewicz.cache.CacheServer;
import com.nurkiewicz.dao.Person;
import com.nurkiewicz.dao.PersonDao;
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

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/*
 * Tomasz Nurkiewicz
 * @tnurkiewicz
 * www.nurkiewicz.com
 * Author of 'Reactive programming with RxJava'
 *
 */
public class InfoShare {

    public static final File DIR = new File("/Users/tomasz.nurkiewicz/infoshare");

    @Test
    public void infoShare_14() throws Exception {
        CompletableFuture<String> future = completedFuture("abc");

        //blocking... millisecond, seconds
        final String string = future.get();
    }

    @Test
    public void infoShare_28() throws Exception {
        final CompletableFuture<BigDecimal> decimal =
                CompletableFuture.supplyAsync(() -> {
                    //some logic...
                    return BigDecimal.TEN;
                } /* beware */);

//        ForkJoinPool.commonPool();

        CompletableFuture<Long> lng = decimal
                .thenApply((BigDecimal b) -> b.longValue());
    }

    @Test
    public void infoShare_43() throws Exception {
        //Flowable
        final Observable<Integer> ints = Observable.just(2, 4, 7);
        ints
                .map(x -> x + 10)
//                .filter()
                .subscribe(i -> System.out.println(i));
    }

    WeatherClient weatherClient = new WeatherClient();

    private static final Logger log = LoggerFactory.getLogger(InfoShare.class);

    @Test
    public void infoShare_54() throws Exception {
        log.info("Starting...");
        final Weather gdansk = weatherClient.fetch("Gdańsk");
        log.info("Weather: {}", gdansk);
    }


    @Test
    public void infoShare_69() throws Exception {
        final Observable<Weather> gdansk = weatherClient.rxFetch("Gdańsk");

        gdansk.subscribe((Weather w) -> {
            System.out.println(w);
        });
    }

    @Test
    public void infoShare_79() throws Exception {
        final Observable<Weather> gdansk =
                weatherClient.rxFetch("Gdańsk");

        //weatherClient.fetch("Gdańsk");
        final Observable<Weather> weather = gdansk
                .map(w -> w);
//                .timeout(500, MILLISECONDS);

        weather.subscribe();

        SECONDS.sleep(10);
    }

    PersonDao personDao = new PersonDao();

    @Test
    public void infoShare_96() throws Exception {
        final Observable<Weather> weather = weatherClient
                .rxFetch("Gdansk")
                .subscribeOn(Schedulers.io());
        final Observable<Person> person =
                personDao
                        .rxFindById(42)
                        .subscribeOn(Schedulers.computation());

        //Flowable
        Observable<String> zipped = weather
                .zipWith(person, (Weather w, Person p) -> {
                    return w.toString() + p.toString();
                });

        zipped.subscribe(System.out::println);
        SECONDS.sleep(4);
    }

    @Test
    public void infoShare_120() throws Exception {
        CacheServer gdansk = new CacheServer();
        CacheServer newYork = new CacheServer();
        final Observable<String> gdanskResult = gdansk.rxFindBy(42);
        final Observable<String> newYorkResult = newYork.rxFindBy(42);


        final Observable<String> results =
                gdanskResult
                        .mergeWith(newYorkResult)
                        .first();
    }


    @Test
    public void infoShare_135() throws Exception {
        System.out.println(listFiles(DIR));
    }

    @Test
    public void infoShare_146() throws Exception {
        Observable
                .interval(1, SECONDS)
                .subscribe(x -> log.info("Num: {}", x));

        TimeUnit.SECONDS.sleep(10);
    }

    void print(Object object) {
        log.info("" + object);
    }

    @Test
    public void infoShare_156() throws Exception {
        Observable
                .interval(1, SECONDS)
                .map(x -> listFiles(DIR))
                .concatMapIterable(list -> list)
                .distinct()  //memory leaks!!!
//                .distinctUntilChanged()
                .toBlocking()
                .subscribe(this::print);
    }

    List<String> listFiles(File dir) {
        final String[] list = dir.list();
        return Arrays.asList(list);
    }

    @Test
    public void infoShare_177() throws Exception {
        final DefaultCamelContext camel = new DefaultCamelContext();

        //Couchbase, MongoDB, RxNetty, RxAndroid
        final ReactiveCamel rxCamel = new ReactiveCamel(camel);
        final Observable<Message> msg = rxCamel
                .toObservable("file:///Users/tomasz.nurkiewicz/infoshare");

        msg.subscribe(this::print);

        TimeUnit.SECONDS.sleep(100);
    }

    @Test
    public void infoShare_194() throws Exception {
        final DefaultCamelContext camel = new DefaultCamelContext();

        //Couchbase, MongoDB, RxNetty, RxAndroid
        final ReactiveCamel rxCamel = new ReactiveCamel(camel);
        rxCamel
                .toObservable("activemq:queue:infoshare")
                .map(Message::getBody)
                .subscribe(this::print);

        TimeUnit.SECONDS.sleep(100);
    }

    @Test
    public void infoShare_208() throws Exception {
        //Awaitility

        final TestScheduler clock = Schedulers.test();
        final Observable<Weather> weather = weatherClient
                .rxFetch("Gdańsk")
                .subscribeOn(Schedulers.io())
                .timeout(500, MILLISECONDS, clock)
                /*.retry(4)*/;

        final TestSubscriber<Weather> subscriber = new TestSubscriber<>();
        weather.subscribe(subscriber);

        subscriber.assertNoErrors();
        clock.advanceTimeBy(499, MILLISECONDS);
        subscriber.assertNoErrors();
        clock.advanceTimeBy(1, MILLISECONDS);
        subscriber.assertError(TimeoutException.class);



    }


    LocalDate tomorrow() throws InterruptedException {
        TimeUnit.DAYS.sleep(1);
        return LocalDate.now();
    }

}











