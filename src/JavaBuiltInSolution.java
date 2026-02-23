import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Readers-Writers Problem using Java's Built-in ReadWriteLock
 * 
 * Java provides a ReadWriteLock interface with ReentrantReadWriteLock
 * implementation.
 * This is the recommended approach for production code.
 * 
 * Features:
 * - Thread-safe and well-tested
 * - Fair/non-fair modes available
 * - Supports lock reentrance
 * - Better performance than manual implementation
 */
public class JavaBuiltInSolution {

    // Shared resource (database)
    static class Database {
        private String data = "Initial Data";
        private final ReadWriteLock lock = new ReentrantReadWriteLock(true); // true = fair mode

        public String read() {
            lock.readLock().lock();
            try {
                return data;
            } finally {
                lock.readLock().unlock();
            }
        }

        public void write(String newData) {
            lock.writeLock().lock();
            try {
                this.data = newData;
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    // Reader Thread
    static class Reader implements Runnable {
        private final int id;
        private final Database database;

        public Reader(int id, Database database) {
            this.id = id;
            this.database = database;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 3; i++) {
                    System.out.println("Reader " + id + " is waiting to read...");

                    // The locking is handled inside the database methods
                    String data = database.read();

                    // Reading section
                    System.out.println(">>> Reader " + id + " is READING: " + data);
                    Thread.sleep(700); // Simulate reading time

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

        public Writer(int id, Database database) {
            this.id = id;
            this.database = database;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 2; i++) {
                    System.out.println("Writer " + id + " is waiting to write...");

                    // Writing section
                    String newData = "Data from Writer " + id + " (iteration " + (i + 1) + ")";
                    System.out.println("*** Writer " + id + " is WRITING: " + newData);
                    database.write(newData);
                    Thread.sleep(1000); // Simulate writing time

                    System.out.println("Writer " + id + " finished writing.");
                    Thread.sleep(600);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== JAVA BUILT-IN ReadWriteLock SOLUTION ===");
        System.out.println("Using ReentrantReadWriteLock in FAIR mode.");
        System.out.println("This is the recommended approach for production code.\n");

        Database database = new Database();

        // Create reader and writer threads
        Thread reader1 = new Thread(new Reader(1, database));
        Thread reader2 = new Thread(new Reader(2, database));
        Thread reader3 = new Thread(new Reader(3, database));
        Thread writer1 = new Thread(new Writer(1, database));
        Thread writer2 = new Thread(new Writer(2, database));

        // Start threads
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
