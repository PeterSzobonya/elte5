import java.util.Arrays;

class Main{
    private static int MAX_ELEMENT = Integer.MIN_VALUE;

    public static void main(String[] args) throws InterruptedException {
        // random array
        int[] arr = {1,2,3,10,4,5,6,7,8,9};

        // split the array in the middle
        int[] partOne = Arrays.copyOfRange(arr, 0, arr.length/2);
        int[] partTwo = Arrays.copyOfRange(arr, (arr.length / 2), arr.length);

        // create runnables
        MaxSearch calcOne = new MaxSearch(partOne);
        MaxSearch calcTwo = new MaxSearch(partTwo);

        // create threads
        Thread threadOne = new Thread(calcOne);
        Thread threadTwo = new Thread(calcTwo);

        // start threads
        threadOne.start();
        threadTwo.start();

        // wait for threads to complete
        threadOne.join();
        threadTwo.join();

        // print results
        System.out.println("Max element from the array: " + MAX_ELEMENT);
    }

    private static class MaxSearch implements Runnable {
        private int[] elements;

        public MaxSearch(int[] elements) {
            this.elements = elements;
        }

        @Override
        public void run() {
            // getting max element from part of the array
            Integer max = Arrays.stream(elements).reduce(Integer::max).orElse(Integer.MIN_VALUE);
            // call synced method
            setNewMax(max);
        }

        // syncronized method to alter the MAX_ELEMENT
        private synchronized void setNewMax(int max) {
            MAX_ELEMENT = max > MAX_ELEMENT ? max : MAX_ELEMENT;
        }

    }
}