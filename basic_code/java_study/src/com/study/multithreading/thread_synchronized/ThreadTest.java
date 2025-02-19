package com.study.multithreading.thread_synchronized;

public class ThreadTest {
    public static void main(String[] args) {
        Account account = new Account("ICBC-110",100000);
        new DrawThread(account,"user1").start();
        new DrawThread(account,"user2").start();
    }
}
