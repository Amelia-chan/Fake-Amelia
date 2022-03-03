package pw.mihou.alisa.modules.threadpools;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AlisaThreadPool {


    private static final int CORE_POOL_SIZE = 1;
    private static final int MAXIMUM_POOL_SIZE = Integer.MAX_VALUE;
    private static final int KEEP_ALIVE_TIME = 120;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    private static final ExecutorService executorService = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TIME_UNIT,
            new SynchronousQueue<>(),
            new ThreadFactory(
                    "Alisa - Executor - %d",
                    false
            )
    );

    private static final ScheduledExecutorService executor =
            Executors.newScheduledThreadPool(
                    1,
                    new ThreadFactory(
                            "Alisa - Central Scheduler - %d",
                            false
                    )
            );

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    /**
     * Shutdowns the scheduled executor service.
     * Already called when exiting.
     */
    public static void shutdown() {
        executor.shutdown();
    }

    /**
     * Schedules a task at a fixed rate.
     *
     * @param task        the task to schedule.
     * @param delay       the delay before the first run.
     * @param time        the repeat time.
     * @param measurement the time measurement.
     * @return ScheduledFuture<?>
     */
    public static ScheduledFuture<?> schedule(Runnable task, long delay, long time, TimeUnit measurement) {
        return executor.scheduleAtFixedRate(task, delay, time, measurement);
    }

    /**
     * Runs a single task on a delay.
     *
     * @param task        the task to run.
     * @param delay       the delay before the first execution.
     * @param measurement the time measurement.
     * @return ScheduledFuture<?>
     */
    public static ScheduledFuture<?> schedule(Runnable task, long delay, TimeUnit measurement) {
        return executor.schedule(task, delay, measurement);
    }

    /**
     * Submits a task to run asynchronous.
     *
     * @param task the task to run.
     * @return CompletableFuture<Void>
     */
    public static CompletableFuture<Void> submitTask(Runnable task) {
        return CompletableFuture.runAsync(task, executor);
    }

    /**
     * Returns the ScheduledExecutorService.
     *
     * @return ScheduledExecutorService.
     */
    public ScheduledExecutorService getScheduler() {
        return executor;
    }

    static class ThreadFactory implements java.util.concurrent.ThreadFactory {

        /**
         * The numbering counter.
         */
        private final AtomicInteger counter = new AtomicInteger();

        /**
         * The name pattern.
         */
        private final String namePattern;

        /**
         * Whether to create daemon threads.
         */
        private final boolean daemon;

        /**
         * Creates a new thread factory.
         *
         * @param namePattern The name pattern, may contain a {@code %d} wildcard where the counter gets filled in.
         * @param daemon      Whether to create daemon or non-daemon threads.
         */
        public ThreadFactory(String namePattern, boolean daemon) {
            this.namePattern = namePattern;
            this.daemon = daemon;
        }

        @Override
        public Thread newThread(@NotNull Runnable r) {
            Thread thread = new Thread(r, String.format(namePattern, counter.incrementAndGet()));
            thread.setDaemon(daemon);
            return thread;
        }
    }

}
