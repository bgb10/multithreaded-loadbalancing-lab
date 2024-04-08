public class pc_static_block {
    private static int NUM_END = 200000; // default input
    private static int NUM_THREADS = 4;

    public static void main(String[] args) {
        if (args.length == 2) {
            NUM_THREADS = Integer.parseInt(args[0]);
            NUM_END = Integer.parseInt(args[1]);
        }
        int counter = 0;
        long startTime = System.currentTimeMillis();

        // Calculate block size
        int blockSize = NUM_END / NUM_THREADS;

        // Create an array to hold references to the threads
        Thread[] threads = new Thread[NUM_THREADS];

        // Create an array to hold counts from each thread
        // count not shared for each other, no need to synchronize
        int[] counts = new int[NUM_THREADS];

        // Start and join threads
        for (int i = 0; i < NUM_THREADS; i++) {
            int start = i * blockSize;
            int end = (i == NUM_THREADS - 1) ? NUM_END : (i + 1) * blockSize;
            threads[i] = new Thread(new PrimeCounter(start, end, counts, i));
            threads[i].start();
        }

        // Join all threads, thus blocks until called thread ends.
        for (int i = 0; i < NUM_THREADS; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Count primes from all threads
        for (int i = 0; i < NUM_THREADS; i++) {
            counter += counts[i];
        }

        long endTime = System.currentTimeMillis();
        long timeDiff = endTime - startTime;
        System.out.println("Program Execution Time: " + timeDiff + "ms");
        System.out.println("1..." + (NUM_END - 1) + " prime# counter=" + counter);
    }

    private static class PrimeCounter implements Runnable {
        private final int start;
        private final int end;
        private final int[] counts;
        private final int threadNum;

        PrimeCounter(int start, int end, int[] counts, int threadNum) {
            this.start = start;
            this.end = end;
            this.counts = counts;
            this.threadNum = threadNum;
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            int localCount = 0;
            for (int i = start; i < end; i++) {
                if (isPrime(i)) localCount++;
            }
            counts[threadNum] = localCount;
            long endTime = System.currentTimeMillis();
            long timeDiff = endTime - startTime;
            System.out.println("Thread " + threadNum + " execution time: " + timeDiff + "ms");
        }

        private static boolean isPrime(int x) {
            if (x <= 1) return false;
            for (int i = 2; i < x; i++) {
                if (x % i == 0) return false;
            }
            return true;
        }
    }
}
