import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

class Main{
    private static List<Integer> numbers = new ArrayList<>();


    public static void main(String[] args) {
        new Thread(new AddOneEverySecond()).start();
        new Thread(new RemoveEveryEven()).start();

        
        while(true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized(numbers){
                System.out.println(numbers);
            }
        }
    }

    private static class AddOneEverySecond implements Runnable{

        @Override
        public void run() {
            Random random = new Random();   
            while(true) {
                // sleep one second
                try {
                    Thread.sleep(1000);;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // generate random number in 0;99 range
                int randomNumber = random.nextInt(99);

                synchronized(numbers) {
                    numbers.add(numbers.size(), randomNumber);
                }
            }   
        }

    }

    private static class RemoveEveryEven implements Runnable{

        @Override
        public void run() {
            while(true) {
                // sleep for 2 seconds
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized(numbers) {
                    numbers = numbers.stream().filter(number -> number % 2 != 0).collect(Collectors.toList());
                }
            }
        }
        
    }
}