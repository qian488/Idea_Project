package com.study.multithreading.thread_synchronized;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
    private String cardId;
    private double balance;

    private final Lock lock = new ReentrantLock();

    public Account() {
    }

    public Account(String cardId, double balance) {
        this.balance = balance;
        this.cardId = cardId;
    }
    // 同步方法
/*
    public synchronized void drawMoney(double balance) {
        String name = Thread.currentThread().getName();
        if(this.balance >= balance) {
            System.out.println(name + "来取钱，" + balance + "已取出");
            this.balance -= balance;
            System.out.println("余额剩余" + this.balance);
        }else {
            System.out.println(name + "来取钱，余额不足");
        }
    }
*/
    // 同步代码块
/*
    public void drawMoney(double balance) {
        String name = Thread.currentThread().getName();
        synchronized (this) {
            if(this.balance >= balance) {
                System.out.println(name + "来取钱，" + balance + "已取出");
                this.balance -= balance;
                System.out.println("余额剩余" + this.balance);
            }else {
                System.out.println(name + "来取钱，余额不足");
            }
        }
    }
*/
    public void drawMoney(double balance) {
        String name = Thread.currentThread().getName();

        try{
            lock.lock();

            if(this.balance >= balance) {
                System.out.println(name + "来取钱，" + balance + "已取出");
                this.balance -= balance;
                System.out.println("余额剩余" + this.balance);
            }else {
                System.out.println(name + "来取钱，余额不足");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally{
            lock.unlock();
        }
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
