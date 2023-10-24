package course.concurrency.tests.future;

import java.util.concurrent.*;

public class FutureCancellation {

    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor executorService = new ThreadPoolExecutor(1, 1, 1, TimeUnit.HOURS, new LinkedBlockingQueue<>(1));

        executorService.submit(() -> {
            Thread.sleep(5000);
            return "Hello";
        });

        Future<String> second = executorService.submit(() -> {
            System.out.println(Thread.currentThread().getName());
            return "not executed";
        });

        second.cancel(false);

        executorService.shutdown();
        executorService.awaitTermination(10_000, TimeUnit.MILLISECONDS);
    }

}
