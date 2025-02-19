package com.study.multithreading.thread_pool;

import java.util.concurrent.Callable;

public class MyCallalbe implements Callable<String> {
    private int n;
    public MyCallalbe(int n) {
        this.n = n;
    }
    @Override
    public String call() throws Exception {
        int sum = 0;
        for (int i = 0; i <= n; i++) {
            sum += i;
        }
        return Thread.currentThread().getName() + ":求出了 [1 - " + n + "] 的和为" + sum;
    }
}
