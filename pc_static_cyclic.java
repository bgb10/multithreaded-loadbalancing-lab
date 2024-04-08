public class pc_static_cyclic {
    private static int NUM_END = 2000000; // default input
    private static int NUM_THREADS = 4; // default number of threads

    public static void main(String[] args) {
        if (args.length == 2) {
            NUM_THREADS = Integer.parseInt(args[0]);
            NUM_END = Integer.parseInt(args[1]);
        }

        int counter = 0;
        long startTime = System.currentTimeMillis();

        // Create an array to hold references to the threads
        Thread[] threads = new Thread[NUM_THREADS];

        // Create an array to hold counts from each thread
        int[] counts = new int[NUM_THREADS];

        // Start and join threads
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
        for (int i = 0; i < NUM_THREADS; i++) {
            counter += counts[i];
        }

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
            long startTime = System.currentTimeMillis();
            int localCount = 0;
            int start = threadNum * 10 + 1;
            int end = start + 9;

            while (start <= NUM_END) {
                // System.out.println("Thread " + threadNum + " handling range: " + start + " - " + Math.min(end, NUM_END));
                for (int i = start; i <= Math.min(end, NUM_END); i++) {
                    if (isPrime(i)) localCount++;
                }
                start += NUM_THREADS * 10;
                end = start + 9;
            }

            counts[threadNum] = localCount;
            long endTime = System.currentTimeMillis();
            long timeDiff = endTime - startTime;
            System.out.println("Thread " + threadNum + " execution time: " + timeDiff + "ms");
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
