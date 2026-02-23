import java.util.concurrent.Semaphore;

/**
 * Writer-Preference Solution to the Readers-Writers Problem
 * 
 * In this solution, writers have priority over readers.
 * When a writer is waiting, no new readers can start reading.
 * 
 * DRAWBACK: Readers may starve if writers keep arriving.
 */
public class WriterPreferenceSolution {
    
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
    
    // Synchronization controller with writer preference
    static class ReaderWriterLock {
        private int readerCount = 0;
        private int writerCount = 0;
        private Semaphore resourceLock = new Semaphore(1);     // Controls access to shared resource
        private Semaphore readerCountLock = new Semaphore(1);  // Protects readerCount variable
        private Semaphore writerCountLock = new Semaphore(1);  // Protects writerCount variable
        private Semaphore readTryLock = new Semaphore(1);      // Prevents readers when writers are waiting
        
        public void acquireReadLock() throws InterruptedException {
            readTryLock.acquire();  // Check if any writer is waiting
            readerCountLock.acquire();
            readerCount++;
            if (readerCount == 1) {
                // First reader locks the resource from writers
                resourceLock.acquire();
            }
            readerCountLock.release();
            readTryLock.release();
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
            writerCountLock.acquire();
            writerCount++;
            if (writerCount == 1) {
                // First writer blocks new readers
                readTryLock.acquire();
            }
            writerCountLock.release();
            
            // Writer waits for exclusive access
            resourceLock.acquire();
        }
        
        public void releaseWriteLock() throws InterruptedException {
            resourceLock.release();
            
            writerCountLock.acquire();
            writerCount--;
            if (writerCount == 0) {
                // Last writer allows readers
                readTryLock.release();
            }
            writerCountLock.release();
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
                    System.out.println("Reader " + id + " is waiting to read...");
                    lock.acquireReadLock();
                    
                    // Reading section
                    System.out.println(">>> Reader " + id + " is READING: " + database.read());
                    Thread.sleep(800);  // Simulate reading time
                    
                    lock.releaseReadLock();
                    System.out.println("Reader " + id + " finished reading.");
                    
                    Thread.sleep(400);
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
                    System.out.println("Writer " + id + " is waiting to write...");
                    lock.acquireWriteLock();
                    
                    // Writing section
                    String newData = "Data from Writer " + id + " (iteration " + (i+1) + ")";
                    System.out.println("*** Writer " + id + " is WRITING: " + newData);
                    database.write(newData);
                    Thread.sleep(1200);  // Simulate writing time
                    
                    lock.releaseWriteLock();
                    System.out.println("Writer " + id + " finished writing.");
                    
                    Thread.sleep(800);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== WRITER-PREFERENCE SOLUTION ===");
        System.out.println("In this solution, writers have priority.");
        System.out.println("Readers may starve if writers keep arriving.\n");
        
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
        reader2.start();
        writer1.start();
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
