package com.study.multithreading.thread_pool;

import java.util.concurrent.*;

public class ThreadPoolTest1 {
    public static void main(String[] args) {
        try (ExecutorService pool = new ThreadPoolExecutor(
                3,
                5,
                8,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(4),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy())) {

            Runnable target = new MyRunnable();
            pool.execute(target);
            for (int i = 0; i < 10; i++) {
                pool.execute(target);
            }
            pool.shutdown();

        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
