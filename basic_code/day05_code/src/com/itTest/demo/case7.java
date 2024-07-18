package com.itTest.demo;

public class case7 {
    public static void main(String[] args) {
        //双色球案例
        //判断中奖情况,传入两组号码，判断中奖情况，并输出
        int[] userNumbers = case5.userSelectNumber();
        int[] luckNumbers = case6.createLuckNumbers();
        judge(userNumbers,luckNumbers);
    }
    public static void judge(int[] userNumbers,int[] LuckNumbers)
    {
        int redCount = 0;
        int blueCount = 0;
        for (int i = 0; i <userNumbers.length-1 ; i++)
        {
            for (int j = 0; j < LuckNumbers.length-1; j++) {
                if (userNumbers[i] == LuckNumbers[j])
                {
                    redCount++;
                    break;
                }
            }
        }
        blueCount=userNumbers[6]==LuckNumbers[6]?1:0;
        System.out.println("您命中的红球数量："+redCount);
        System.out.println("您命中的蓝球数量："+blueCount);
        if (redCount==6&&blueCount==1)
        {
            System.out.println("恭喜您，中奖1000万");
        }
        else if (redCount == 6&&blueCount == 0)
        {
            System.out.println("恭喜您，中奖500万");
        }
        else if (redCount == 5&&blueCount == 1)
        {
            System.out.println("恭喜您，中奖3000");
        }
        else if ((redCount == 5&&blueCount == 0)||(redCount == 4&& blueCount == 1))
        {
            System.out.println("恭喜您，中奖200");
        }
        else if ((redCount == 4&&blueCount == 0)||(redCount == 3&& blueCount == 1))
        {
            System.out.println("恭喜您，中奖10");
        }
        else if (redCount < 3&&blueCount == 1)
        {
            System.out.println("恭喜您，中奖5");
        }
        else
        {
            System.out.println("很遗憾，您没有中奖");
        }
    }
}
