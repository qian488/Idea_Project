package com.study.multithreading.thread_OptimisticLocks;

import java.util.concurrent.atomic.AtomicInteger;

public class MyRunnable implements Runnable {
    private AtomicInteger id = new AtomicInteger();

    @Override
    public void run(){
        for (int i = 0; i < 100; i++) {
            System.out.println(Thread.currentThread().getName() + ":" + (id.incrementAndGet()));
        }
    }
}
