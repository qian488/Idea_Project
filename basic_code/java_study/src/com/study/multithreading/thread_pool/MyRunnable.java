package com.study.multithreading.thread_pool;

public class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " ===> running");
        try{
            Thread.sleep(10000);
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }
}
