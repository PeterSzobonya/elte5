import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(
            new Writer(), 
            0, 
            Duration.ofSeconds(10).toSeconds(), 
            TimeUnit.SECONDS);
    }

    private static class Writer implements Runnable {
        @Override
        public void run() {
            System.out.println(Instant.now());            
        }
    }
}