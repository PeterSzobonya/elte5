

public class Threads {
    public static void main(String[] args) {
        Thread t = new MyThread();
        t.start();

        Thread tt = new Thread(new MyRunnable());
        tt.start();

        System.out.println("Hello from main! " + Thread.currentThread().getName());
    }

    private static class MyThread extends Thread {

        @Override
        public void run() {
            System.out.println("Hello from MyThread! " + Thread.currentThread().getName());
        }
    }

    private static class MyRunnable implements Runnable {

        @Override
        public void run() {
            System.out.println("Hello from MyRunnable! " + Thread.currentThread().getName());
        }
    }
}
