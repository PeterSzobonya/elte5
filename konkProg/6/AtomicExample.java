package hu.pp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicExample {
    public static void main(String[] args) throws InterruptedException {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        List<Integer> numbers = new ArrayList<>();
        for(int i =0; i<100; ++i) {
            numbers.add(i & 7);
        }
        Thread t1 = new Thread(()->search(numbers, 0, 50, atomicInteger));
        t1.start();
        Thread t2 = new Thread(()->search(numbers, 50, 100, atomicInteger));
        t2.start();
        t1.join();
        t2.join();
        System.out.println(atomicInteger.get());
    }

    static void search(List<Integer> numbers, int from, int to, AtomicInteger atomicInteger) {
        for(int i=from; i<to; ++i) {
            boolean shouldCheck = true;
            do {
                int current = atomicInteger.get();
                if (current < numbers.get(i)) {
                    shouldCheck = !atomicInteger.compareAndSet(current, numbers.get(i));
                } else {
                    shouldCheck = false;
                }
            } while (shouldCheck);
        }
    }
}
