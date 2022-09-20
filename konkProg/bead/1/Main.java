import java.io.InputStreamReader;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(new InputStreamReader(System.in));
            System.out.println("Kerem adja meg a szalak szamat: ");
            int n = scanner.nextInt();
            System.out.println("Inditott szalak szama: " + n);

            for (int i = 0; i < n; i++) {
                new Thread( new MyRunnable(i)).start();
            }


        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    private static class MyRunnable implements Runnable {
        private int runnableId;

        public MyRunnable(int runnableId) {
            this.runnableId = runnableId;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                System.out.println("Thread with the id: " + runnableId + ", from " + Thread.currentThread().getName());
            }
        }
    }
}