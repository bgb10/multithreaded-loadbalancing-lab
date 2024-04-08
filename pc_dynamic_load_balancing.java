import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class pc_dynamic_load_balancing {
    private static int NUM_END = 200000; // default input
    private static int NUM_THREADS = 4; // default number of threads
    private static int[] counts = new int[NUM_THREADS]; // Array to hold counts from each thread
    private static BlockingQueue<Integer> taskQueue = new LinkedBlockingQueue<>(); // Task pool

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis(); // Start time declaration

        if (args.length == 2) {
            NUM_THREADS = Integer.parseInt(args[0]);
            NUM_END = Integer.parseInt(args[1]);
        }

        // Populate task queue with numbers
        for (int i = 1; i <= NUM_END; i += 10) {
            taskQueue.offer(i);
        }

        // Create an array to hold references to the threads
        Thread[] threads = new Thread[NUM_THREADS];

        // Start threads
        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i] = new Thread(new PrimeCounter(i, counts));
            threads[i].start();
        }

        // Join all threads
        for (int i = 0; i < NUM_THREADS; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Count primes from all threads
        int counter = 0;
        for (int i = 0; i < NUM_THREADS; i++) {
            counter += counts[i];
        }

        // Print results
        long endTime = System.currentTimeMillis();
        long timeDiff = endTime - startTime;
        System.out.println("Program Execution Time: " + timeDiff + "ms");
        System.out.println("1..." + (NUM_END - 1) + " prime# counter=" + counter);
    }

    private static class PrimeCounter implements Runnable {
        private final int threadNum;
        private final int[] counts;

        PrimeCounter(int threadNum, int[] counts) {
            this.threadNum = threadNum;
            this.counts = counts;
        }

        @Override
        public void run() {
            while (true) {
                Integer startNumber = getNextNumber();
                if (startNumber == null) break; // No more numbers to process
                int localCount = 0;
                // System.out.println("Thread " + threadNum + " handling range: " + startNumber + " - " + Math.min(startNumber + 10, NUM_END));
                for (int i = startNumber; i < startNumber + 10; i++) {
                    if (i <= NUM_END && isPrime(i)) {
                        localCount++;
                    }
                }
                counts[threadNum] += localCount;
            }
        }

        private Integer getNextNumber() {
            synchronized (taskQueue) {
                // Retrieve the start number of the range from the task queue
                return taskQueue.poll();
            }
        }

        private static boolean isPrime(int x) {
            if (x <= 1) return false;
            for (int i = 2; i <= Math.sqrt(x); i++) {
                if (x % i == 0) return false;
            }
            return true;
        }
    }
}
