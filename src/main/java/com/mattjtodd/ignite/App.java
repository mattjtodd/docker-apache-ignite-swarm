package com.mattjtodd.ignite;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Ignition!
 */
public class App {

    public static void main(String[] args) throws Exception {
        Logger anonymousLogger = Logger.getLogger(App.class.getName());
        anonymousLogger
                .info("************" + args[0]);

        ApplicationContext applicationContext = new GenericXmlApplicationContext(args[0]);

        IgniteConfiguration bean = applicationContext.getBean(IgniteConfiguration.class);

        Ignite ignite = Ignition.start(bean);

        CacheConfiguration<Object, Object> configuration = new CacheConfiguration<>("Mince");
        configuration.setCacheMode(CacheMode.REPLICATED);
        IgniteCache<Object, Object> mince = ignite
                .getOrCreateCache(configuration);

        anonymousLogger
                .severe("************ CREATE CACHE");

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

        AtomicInteger atomicInteger = new AtomicInteger(0);
        scheduledExecutorService
                .scheduleAtFixedRate(() -> {
                    int i = atomicInteger.incrementAndGet();
                    mince.putIfAbsent(String.valueOf(i), "" + RandomStringUtils.randomAlphabetic(10000));
                }, 0, 10, MILLISECONDS);

        scheduledExecutorService
                .scheduleAtFixedRate(() -> System.out.println(mince), 0, 5, SECONDS);

    }

}
