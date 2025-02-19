package com.study.multithreading.thread_pool;

import java.util.concurrent.*;

public class ThreadPoolTest3 {
    public static void main(String[] args) throws Exception{
        ExecutorService pool = Executors.newFixedThreadPool(3);

        Future<String> f1 = pool.submit(new MyCallalbe(100));
        Future<String> f2 = pool.submit(new MyCallalbe(200));
        Future<String> f3 = pool.submit(new MyCallalbe(300));
        Future<String> f4 = pool.submit(new MyCallalbe(400));

        System.out.println(f1.get());
        System.out.println(f2.get());
        System.out.println(f3.get());
        System.out.println(f4.get());

    }
}
