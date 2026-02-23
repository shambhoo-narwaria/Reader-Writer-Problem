import java.util.concurrent.Semaphore;

/**
 * Fair/Starve-Free Solution to the Readers-Writers Problem
 * 
 * This solution ensures fairness by using a FIFO ordering semaphore.
 * Neither readers nor writers will starve.
 * Threads are served in the order they arrive.
 */
public class FairSolution {

    // Shared resource (database)
    static class Database {
        private String data = "Initial Data";

        public String read() {
            return data;
        }

        public void write(String newData) {
            this.data = newData;
        }
    }

    // Fair synchronization controller
    static class FairReaderWriterLock {
        private int readerCount = 0;
        private Semaphore orderLock = new Semaphore(1); // Ensures FIFO ordering
        private Semaphore resourceLock = new Semaphore(1); // Controls access to shared resource
        private Semaphore readerCountLock = new Semaphore(1); // Protects readerCount variable

        public void acquireReadLock() throws InterruptedException {
            orderLock.acquire(); // Enter the queue (FIFO)
            readerCountLock.acquire();
            readerCount++;
            if (readerCount == 1) {
                // First reader locks the resource from writers
                resourceLock.acquire();
            }
            readerCountLock.release();
            orderLock.release(); // Allow next thread in queue
        }

        public void releaseReadLock() throws InterruptedException {
            readerCountLock.acquire();
            readerCount--;
            if (readerCount == 0) {
                // Last reader releases the resource for writers
                resourceLock.release();
            }
            readerCountLock.release();
        }

        public void acquireWriteLock() throws InterruptedException {
            orderLock.acquire(); // Enter the queue (FIFO)
            resourceLock.acquire(); // Wait for exclusive access
            orderLock.release(); // Allow next thread in queue
        }

        public void releaseWriteLock() {
            resourceLock.release();
        }
    }

    // Reader Thread
    static class Reader implements Runnable {
        private final int id;
        private final Database database;
        private final FairReaderWriterLock lock;

        public Reader(int id, Database database, FairReaderWriterLock lock) {
            this.id = id;
            this.database = database;
            this.lock = lock;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 3; i++) {
                    System.out.println("Reader " + id + " is waiting to read...");
                    lock.acquireReadLock();

                    // Reading section
                    System.out.println(">>> Reader " + id + " is READING: " + database.read());
                    Thread.sleep(700); // Simulate reading time

                    lock.releaseReadLock();
                    System.out.println("Reader " + id + " finished reading.");

                    Thread.sleep(300);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // Writer Thread
    static class Writer implements Runnable {
        private final int id;
        private final Database database;
        private final FairReaderWriterLock lock;

        public Writer(int id, Database database, FairReaderWriterLock lock) {
            this.id = id;
            this.database = database;
            this.lock = lock;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 2; i++) {
                    System.out.println("Writer " + id + " is waiting to write...");
                    lock.acquireWriteLock();

                    // Writing section
                    String newData = "Data from Writer " + id + " (iteration " + (i + 1) + ")";
                    System.out.println("*** Writer " + id + " is WRITING: " + newData);
                    database.write(newData);
                    Thread.sleep(1000); // Simulate writing time

                    lock.releaseWriteLock();
                    System.out.println("Writer " + id + " finished writing.");

                    Thread.sleep(600);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== FAIR/STARVE-FREE SOLUTION ===");
        System.out.println("This solution uses FIFO ordering to ensure fairness.");
        System.out.println("Neither readers nor writers will starve.\n");

        Database database = new Database();
        FairReaderWriterLock lock = new FairReaderWriterLock();

        // Create reader and writer threads
        Thread reader1 = new Thread(new Reader(1, database, lock));
        Thread reader2 = new Thread(new Reader(2, database, lock));
        Thread reader3 = new Thread(new Reader(3, database, lock));
        Thread writer1 = new Thread(new Writer(1, database, lock));
        Thread writer2 = new Thread(new Writer(2, database, lock));

        // Start threads in mixed order
        reader1.start();
        writer1.start();
        reader2.start();
        writer2.start();
        reader3.start();

        // Wait for all threads to complete
        try {
            reader1.join();
            reader2.join();
            reader3.join();
            writer1.join();
            writer2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n=== ALL THREADS COMPLETED ===");
        System.out.println("Final data: " + database.read());
    }
}
