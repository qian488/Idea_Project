package com.itTest.demo;

import java.util.Random;
import java.util.Scanner;

public class case2 {
    public static void main(String[] args) {
        //抢红包案例plus
        int[] moneys = {9,666,188,520,99999};
        start(moneys);
    }

    public static void start(int[] moneys)
    {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        int index = random.nextInt(moneys.length);
        //1.先打乱数组的顺序
        for (int i = 0 ; i < moneys.length  ; i++) {
            int temp = moneys[index];
            moneys[index] = moneys[i];
            moneys[i] = temp;
        }
        //2.输出打乱后的顺序
        for (int i = 0; i < 5; i++) {
            System.out.println("请输入内容进行抽奖：");
            scanner.next();//等待用户输入，按了回车继续进行

            int money = moneys[i];
            System.out.println("恭喜你抽中了红包：" + money);

        }
        System.out.println("抽奖结束！");
    }
}
