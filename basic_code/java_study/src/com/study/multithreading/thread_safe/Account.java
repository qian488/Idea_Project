package com.study.multithreading.thread_safe;

public class Account {
    private String cardId;
    private double balance;

    public Account() {
    }

    public Account(String cardId, double balance) {
        this.balance = balance;
        this.cardId = cardId;
    }

    public void drawMoney(double balance) {
        String name = Thread.currentThread().getName();
        if(this.balance >= balance) {
            System.out.println(name + "来取钱，" + balance + "已取出");
            this.balance -= balance;
            System.out.println("余额剩余" + this.balance);
        }else {
            System.out.println(name + "来取钱，余额不足");
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
