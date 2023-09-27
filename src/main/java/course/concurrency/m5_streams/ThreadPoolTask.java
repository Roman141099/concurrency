package course.concurrency.m5_streams;

import java.util.concurrent.*;

public class ThreadPoolTask {

    // Task #1
    public ThreadPoolExecutor getLifoExecutor() {
        return new ThreadPoolExecutor(
                1,
                1,
                10,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>() {
                    @Override
                    public boolean offer(Runnable runnable) {
                        return super.offerFirst(runnable);
                    }
                }
        );
    }

    // Task #2
    public ThreadPoolExecutor getRejectExecutor() {
        return new ThreadPoolExecutor(
                8,
                8,
                10,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1) {

                    @Override
                    public boolean offer(Runnable runnable) {
                        return false;
                    }
                },
                (r, c) -> {
                    System.out.println("Discarded");
                }
        );
    }

}
