package com.itTest.demo;

import java.util.Random;
import java.util.Scanner;

public class case1 {
    public static void main(String[] args) {
        //抢红包案例
        int[] moneys = {9,666,188,520,99999};
        start(moneys);
    }
    public static void start(int[] moneys)
    {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        //1.定义一个for循环，控制抽奖5次
        for (int i = 0; i < 5; i++) {
            //2.提示抽奖
            System.out.println("请输入内容进行抽奖：");
            scanner.next();//等待用户输入，按了回车继续进行

            //3.为当前粉丝找一个随机的红包出来
            while (true) {
                int index = random.nextInt(moneys.length);
                int money = moneys[index];

                //4.判断红包是否不为零
                if (money != 0)
                {
                    System.out.println("恭喜您抽中了红包，" + money);
                    moneys[index] = 0;
                    break;//结束这次抽奖
                }
            }
        }
        System.out.println("活动结束！");
    }
}
