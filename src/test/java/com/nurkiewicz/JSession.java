package com.nurkiewicz;

import com.nurkiewicz.cache.CacheServer;
import com.nurkiewicz.weather.Weather;
import com.nurkiewicz.weather.WeatherClient;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.rx.ReactiveCamel;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class JSession {

    public static final File DIR = new File("/Users/tomasz.nurkiewicz/workspace/personal/jsession");
    private WeatherClient client = new WeatherClient();
    private CacheServer cacheServer;

    @Test
    public void jsession_6() throws Exception {
        final CompletableFuture<String> f = CompletableFuture.completedFuture("abc"); //callDb(sql)
        final String str = f.get();     //blokujące

        final CompletableFuture<Integer> int2 = f.thenApply((String s) -> s.length());
    }

    @Test
    public void jsession_18() throws Exception {
        final CompletableFuture<Integer> comp = CompletableFuture.supplyAsync(() -> {
            //zawołać bazę danych
            //zawołać WS
            throw new RuntimeException("Opps");
//            return 42;
        });
        ForkJoinPool.commonPool();

        comp.get();
        comp.handle((i, error) -> {
            return null;
        });
    }

    @Test
    public void jsession_35() throws Exception {
        Observable<String> obs = Observable.just("abc");
        final Observable<Integer> ints = Observable.fromCallable(() -> {
            return 2 + 2 * 2;
        });
    }

    @Test
    public void jsession_46() throws Exception {
        Observable<String> obs = Observable.just("abc", "def", "ghi");
    }


    @Test
    public void jsession_49() throws Exception {
//        final Weather bialystok = client.fetch("Bialystok");
        final Observable<Weather> bialystok = client.rxFetch("Bialystok");
        bialystok.subscribe(weather -> System.out.println(weather));
    }

    @Test
    public void jsession_61() throws Exception {
        final Observable<Weather> bialystok = client.rxFetch("Bialystok");
        bialystok
                .timeout(500, TimeUnit.MILLISECONDS)
                .subscribe(System.out::println);
    }

    @Test
    public void jsession_70() throws Exception {
        final Observable<Weather> bialystok = client.rxFetch("Bialystok");
        bialystok
                .subscribeOn(Schedulers.io())
                .subscribe(System.out::println);
        System.out.println("Skończyłem");
        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    public void jsession_81() throws Exception {
        final Observable<Weather> bialystok = client.rxFetch("Bialystok");
        bialystok
                .toBlocking()
                .first();
    }

    @Test
    public void jsession_89() throws Exception {
        CacheServer krakow = new CacheServer();
        CacheServer warsaw = new CacheServer();

        final Observable<String> obs1 = krakow.rxFindBy(42);
        final Observable<String> obs2 = warsaw.rxFindBy(42);

        obs1
                .mergeWith(obs2)
                .first()
                .timeout(50, TimeUnit.MILLISECONDS)
//                .map(xmlString -> parse(xmlString))
                .subscribe(x -> System.out.println("Data: " + x));

//        obs1.subscribe(System.out::println);
//        obs2.subscribe(System.out::println);

        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    public void jsession_111() throws Exception {
        cacheServer = new CacheServer();
        final Observable<Weather> bialystok = client.rxFetch("Bialystok");
        final Observable<String> cacheResult = cacheServer.rxFindBy(44);

//        final Weather złePodejście = bialystok.toBlocking().single();
        final Observable<Integer> zip = Observable
                .zip(
                        bialystok,
                        cacheResult,
                        (Weather w, String s) -> (w.toString() + s).length()
                )/*.onErrorResumeNext()*/;

        zip.subscribe();
    }

    @Test
    public void jsession_129() throws Exception {
        System.out.println(childrenOf(DIR));
    }

    @Test
    public void jsession_138() throws Exception {
        Observable.interval(1, TimeUnit.SECONDS).subscribe(System.out::println);
        TimeUnit.SECONDS.sleep(10);
    }

    @Test
    public void jsession_144() throws Exception {
        Observable
                .interval(1, TimeUnit.SECONDS)
                .map(x -> childrenOf(DIR))
                .subscribe(System.out::println);
        TimeUnit.SECONDS.sleep(100);

    }

    @Test
    public void jsession_145() throws Exception {
        Observable
                .interval(1, TimeUnit.SECONDS)
                .flatMapIterable(x -> childrenOf(DIR))
                .distinct()
                .subscribe(System.out::println);
        TimeUnit.SECONDS.sleep(100);
    }

    @Test
    public void jsession_164() throws Exception {
        final DefaultCamelContext ctx = new DefaultCamelContext();
        final ReactiveCamel camel = new ReactiveCamel(ctx);
        camel
                .toObservable("file:///Users/tomasz.nurkiewicz/workspace/personal/jsession")
                .subscribe(System.out::println);
        TimeUnit.SECONDS.sleep(100);
    }

    @Test
    public void jsession_176() throws Exception {
        final DefaultCamelContext ctx = new DefaultCamelContext();
        final ReactiveCamel camel = new ReactiveCamel(ctx);
        camel
                .toObservable("activemq:queue:jsession")
                .map(Message::getBody)
                .subscribe(System.out::println);
        TimeUnit.SECONDS.sleep(100);
    }
    
    List<String> childrenOf(File file) {
        final String[] files = file.list();
        return Arrays.asList(files);
    }

    @Test
    public void jsession_193() throws Exception {
        final Observable<Weather> bialystok = client.rxFetch("Bialystok");

        final TestScheduler clock = new TestScheduler();
        final Observable<Weather> retry = bialystok
                .timeout(500, TimeUnit.MILLISECONDS, clock)
                .retry(4);

        final TestSubscriber<Weather> subscriber = new TestSubscriber<>();
        retry.subscribe(subscriber);
        subscriber.assertNotCompleted();
        clock.advanceTimeBy(499, TimeUnit.MILLISECONDS);
        subscriber.assertNotCompleted();
        clock.advanceTimeBy(1, TimeUnit.MILLISECONDS);

    }

}
