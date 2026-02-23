import java.util.concurrent.Semaphore;

/**
 * Reader-Preference Solution to the Readers-Writers Problem
 * 
 * In this solution, readers have priority over writers.
 * Multiple readers can read simultaneously, but writers must wait.
 * 
 * DRAWBACK: Writers may starve if readers keep arriving.
 */
public class ReaderPreferenceSolution {
    
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
    
    // Synchronization controller
    static class ReaderWriterLock {
        private int readerCount = 0;
        private Semaphore resourceLock = new Semaphore(1);  // Controls access to shared resource
        private Semaphore readerCountLock = new Semaphore(1);  // Protects readerCount variable
        
        public void acquireReadLock() throws InterruptedException {
            readerCountLock.acquire();
            readerCount++;
            if (readerCount == 1) {
                // First reader locks the resource from writers
                resourceLock.acquire();
            }
            readerCountLock.release();
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
            resourceLock.acquire();
        }
        
        public void releaseWriteLock() {
            resourceLock.release();
        }
    }
    
    // Reader Thread
    static class Reader implements Runnable {
        private final int id;
        private final Database database;
        private final ReaderWriterLock lock;
        
        public Reader(int id, Database database, ReaderWriterLock lock) {
            this.id = id;
            this.database = database;
            this.lock = lock;
        }
        
        @Override
        public void run() {
            try {
                for (int i = 0; i < 3; i++) {
                    // Try to acquire read lock
                    System.out.println("Reader " + id + " is waiting to read...");
                    lock.acquireReadLock();
                    
                    // Reading section (critical section)
                    System.out.println(">>> Reader " + id + " is READING: " + database.read());
                    Thread.sleep(1000);  // Simulate reading time
                    
                    // Release read lock
                    lock.releaseReadLock();
                    System.out.println("Reader " + id + " finished reading.");
                    
                    Thread.sleep(500);  // Wait before next read
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
        private final ReaderWriterLock lock;
        
        public Writer(int id, Database database, ReaderWriterLock lock) {
            this.id = id;
            this.database = database;
            this.lock = lock;
        }
        
        @Override
        public void run() {
            try {
                for (int i = 0; i < 2; i++) {
                    // Try to acquire write lock
                    System.out.println("Writer " + id + " is waiting to write...");
                    lock.acquireWriteLock();
                    
                    // Writing section (critical section)
                    String newData = "Data from Writer " + id + " (iteration " + (i+1) + ")";
                    System.out.println("*** Writer " + id + " is WRITING: " + newData);
                    database.write(newData);
                    Thread.sleep(1500);  // Simulate writing time
                    
                    // Release write lock
                    lock.releaseWriteLock();
                    System.out.println("Writer " + id + " finished writing.");
                    
                    Thread.sleep(1000);  // Wait before next write
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== READER-PREFERENCE SOLUTION ===");
        System.out.println("In this solution, readers have priority.");
        System.out.println("Writers may starve if readers keep arriving.\n");
        
        Database database = new Database();
        ReaderWriterLock lock = new ReaderWriterLock();
        
        // Create reader and writer threads
        Thread reader1 = new Thread(new Reader(1, database, lock));
        Thread reader2 = new Thread(new Reader(2, database, lock));
        Thread reader3 = new Thread(new Reader(3, database, lock));
        Thread writer1 = new Thread(new Writer(1, database, lock));
        Thread writer2 = new Thread(new Writer(2, database, lock));
        
        // Start threads
        reader1.start();
        writer1.start();
        reader2.start();
        reader3.start();
        writer2.start();
        
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
