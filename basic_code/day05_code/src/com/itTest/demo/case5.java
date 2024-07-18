package com.itTest.demo;

import java.util.Scanner;


public class case5 {
    public static void main(String[] args)
    {
        //双色球案例
        //用户投注一组号码，并返回用户的号码
        //6个红球范围为1-33，不能重复，1个蓝球范围1-16之间
        int[] userNumbers = userSelectNumber();
        System.out.println("您的号码为：");
        printArray(userNumbers);
    }
    public static void printArray(int[] arr)
    {
        System.out.print("[");
        for (int i = 0; i <arr.length; i++)
        {
            System.out.print(i==arr.length-1?arr[i]:arr[i]+",");
        }
        System.out.println("]");
    }
    public static int[] userSelectNumber()
    {
        int[] numbers = new int[7];
        Scanner scanner = new Scanner(System.in);
        //红球号码
        for (int i = 0; i < numbers.length-1; i++)
        {
            while (true)
            {
                System.out.println("请您输入第" + (i+1) + "个红球号码（1-33之间，不能重复）：");
                int number = scanner.nextInt();
                if (number < 1 || number  > 33)
                {
                    System.out.println("对不起，你输入的红球号码不在1-33之间，请确认！");
                }
                else
                {
                    if (exist(numbers,number))
                    {
                        System.out.println("对不起，你输入的号码重复了，请确认！");
                    }
                    else
                    {
                        numbers[i]=number;
                        break;
                    }
                }
            }
        }
        //蓝球号码
        while (true)
        {
            System.out.println("请您输入一个蓝球号码（1-16）：");
            int number = scanner.nextInt();
            if (number<1||number>16)
            {
                System.out.println("对不起，你输入的蓝球号码范围不对，请确认！");
            }
            else
            {
                numbers[6]=number;
                break;
            }
        }
        return numbers;
    }
    public static boolean exist(int[] numbers, int number)
    {
        //判断number是否在numbers中存在
        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i]==0)
            {
                break;
            }
            if (numbers[i]==number)
            {
                return true;
            }
        }
        return false;
    }

}
