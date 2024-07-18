package com.itTest.demo;

import java.util.Random;

public class case6 {
    public static void main(String[] args)
    {
        //双色球案例
        //随机生成一组中将号码，返回中奖号码
        int[] LuckNumbers = createLuckNumbers();
        case5.printArray(LuckNumbers);
    }
    public static int[] createLuckNumbers()
    {
        int[] numbers = new int[7];

        Random random = new Random();
        for (int i = 0; i < numbers.length-1; i++)
        {
            while (true) {
                int number = random.nextInt(33)+1;
                if (!case5.exist(numbers,number))
                {
                    numbers[i]=number;
                    break;
                }
            }
        }
        numbers[6]=random.nextInt(16)+1;
        return numbers;
    }
}
