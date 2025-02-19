package com.study.multithreading.thread_OptimisticLocks;

public class Test {
    public static void main(String[] args) {
        Runnable target = new MyRunnable();

        for (int i = 1; i <= 100; i++) {
            new Thread(target).start();
        }
    }
}
