package Tools.Multithread;

import java.util.ConcurrentModificationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Asynchronous Queue
 *<p>
 * Article at: https://www.digitalturbine.com/blog/java-class-for-easy-multithreaded-loops/
 *<p>
 * For multi-threading loops.  Loops that focus on calculations are the best candidates.
 * For example, large List of objects each with a compute() method could be processed
 * in this way.
 *<p>
 * Usage Java 1.8 with lambdas:
 * <p>
 * <pre>
 *     while(...) {
 *         AQ.add(() -> {
 *             //statements
 *         });
 *     }
 *     AQ.finish();
 *</pre>
 * <p>
 * With Java 1.7:
 * <p>
 * <pre>
 *     while(...) {
 *         AQ.add(new Runnable () { public void run() {
 *             //statements
 *         }});
 *     }
 *     AQ.finish();
 * </pre>
 */
public class AQ {
    static ExecutorService es = Executors.newCachedThreadPool();
    static String callingClass = null;
    public static void add(Runnable task) {
        String presentClass = new Throwable().getStackTrace()[1].getClassName();
        presentClass = "Thread " + Thread.currentThread().getId() + ": " + presentClass;
        if (callingClass == null) {
            callingClass = presentClass;
        } else {
            if (!callingClass.equals(presentClass)) {
                throw new ConcurrentModificationException("AQ.add called from multiple classes: " + callingClass + ", " + presentClass);
            }
        }
        es.execute(task);
    }
    /**
     * AQ.finish() must be called after your loop.  This waits until
     * all threads are finished before moving on to the rest of the code.
     */
    public static void finish() {
        // request all threads be completed
        es.shutdown();
        // letting threads complete
        try {
            es.awaitTermination(100, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // resetting threads
        es = Executors.newCachedThreadPool();
        // forget the previous calling class
        callingClass = null;
    }
}
