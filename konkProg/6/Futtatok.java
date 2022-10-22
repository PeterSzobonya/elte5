package hu.pp;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Futtatok {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<Integer>> collected = new ArrayList<>();
        for(int i=0; i<10; ++i) {
            collected.add(executor.submit(() -> {
                Thread.sleep(100);
                System.out.println("1 -- " + Thread.currentThread().getName());
                return 2;
            }));
            collected.add(executor.submit(() -> {
                System.out.println("2 -- " + Thread.currentThread().getName());
                return 3;
            }));
        }
        System.out.println(collected.size() +" -- "+Thread.currentThread().getName());
        for(Future<Integer> fi : collected) {
            System.out.println("content: "+fi.get()+" -- "+Thread.currentThread().getName());
        }

        ScheduledExecutorService ses = Executors.newScheduledThreadPool(2);
        ses.schedule(()->{}, 2_000, TimeUnit.MILLISECONDS);
        ses.scheduleAtFixedRate(()->{
            System.out.println("Hi!");
        }, 0, 3, TimeUnit.SECONDS);
        ses.scheduleWithFixedDelay(()->{}, 0, 3, TimeUnit.SECONDS);
        ses.shutdown();
        executor.shutdownNow();
    }
}
