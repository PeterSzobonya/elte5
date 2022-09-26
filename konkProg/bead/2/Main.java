import java.util.*;

class Main {
    public static void main(String[] args) throws InterruptedException {
        // random array
        int[] arr = {1,2,3,4,5,6,7,8,9};

        // split the array in the middle
        int[] partOne = Arrays.copyOfRange(arr, 0, arr.length/2);
        int[] partTwo = Arrays.copyOfRange(arr, (arr.length / 2), arr.length);
        
        // create runnables
        Calculator calcOne = new Calculator(partOne);
        Calculator calcTwo = new Calculator(partTwo);

        // create threads
        Thread threadOne = new Thread(calcOne);
        Thread threadTwo = new Thread(calcTwo);

        // start threads
        threadOne.start();
        threadTwo.start();

        // wait for threads to complete
        threadOne.join();
        threadTwo.join();

        // print the results
        System.out.println(calcOne.getSum() + calcTwo.getSum());
    }

    private static class Calculator implements Runnable {
        private int[] arr;
        private int sum;

        public Calculator(int[] arr) {
            this.arr = arr;
            this.sum = 0;
        }

        @Override
        public void run() {
            // calculating sum
            sum = Arrays.stream(arr).sum();
        }
        
        public int getSum() {
            return sum;
        }
    }
}