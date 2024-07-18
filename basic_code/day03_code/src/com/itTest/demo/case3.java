package com.itTest.demo;

import java.util.Scanner;

public class case3 {
    public static void main(String[] args) {
        //评委打分的案例
        //总分计算方法，去掉最高分，最低分，再取平均值
         System.out.println("当前选手的得分为："+getAverageScore(6));
    }

    public  static  double getAverageScore(int number)
    {
        //1.定义一个动态初始化的数组，负责存入评委的打分
        int[] scores = new int[number];

        //2.遍历数组，录入分数
        Scanner scanner = new Scanner(System.in);
        for (int i=0;i<scores.length;i++)
        {
            System.out.println("请您录入第" + (i+1) +"个评委的分数");
            int score = scanner.nextInt();
            scores[i]=score;
        }

        //3.计算总分，遍历数组，找出最高分，最低分
        int sum = 0;
        int max = scores[0];
        int min = scores[0];

        for (int i=0;i<scores.length;i++)
        {
            int score = scores[i];
            sum += score;
            if (score > max)
            {
                max = score;
            }
            if (score < min)
            {
                min = score;
            }
        }
        return 1.0*(sum-max-min)/(number-2);
    }
}
