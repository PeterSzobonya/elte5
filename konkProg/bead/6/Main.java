import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/*
Írj egy programot, amiben van két szál, amik egy BlockingQueue -n keresztül kommunikálnak. Az egyik szál 0.5 máosdpercenként generál egy számot [1,100] 
intervallumon, a másik 2 másodpercenként kiveszi az aktuális első értéket, és a négyzetét kiírja a képernyőre. Old meg, hogy 10 számnál több soha ne 
várakozzon a sorban. Ha a sor éppen üres, az elemekre váró szál várjon, amíg új érték érkezik.
*/
class Main {
    public static void main(String[] args) {
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(10);
        ExecutorService executor = Executors.newCachedThreadPool();
        Random random = new Random();

        executor.submit(() -> {
            boolean run  = true;
            while (run) {
                int randInRange = random.nextInt(99) + 1;
                try {
                    queue.put(randInRange);
                    System.out.println("generated: " + randInRange + " queue.size: " + queue.size());
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return 1;
        });

        executor.submit(() -> {
            boolean run = true;
            while (run) {
                int value = queue.take();
                System.out.println("taken pow-2: " + Math.pow(value, 2));
                Thread.sleep(2_000);
            }
            return 1;
        });

    }
}