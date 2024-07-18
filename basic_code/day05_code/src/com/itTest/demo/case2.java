package com.itTest.demo;

public class case2 {
    public static void main(String[] args) {
        //找素数3
        search(100,200);
    }

    public static void search(int start,int end)
    {
        int count = 0;
        for (int i = start; i <= end; i++) {
            //i遍历数据检查是否是素数，是输出，不是就不输出
            if (check(i)) {
                System.out.println(i);
                count++;
            }
        }
        System.out.println("个数为：" + count);
    }

    public static boolean check(int data)
    {
        for (int i = 2; i <= data/2 ; i++) {
            if (data % i == 0) {
                return false;//不是素数
            }

        }
        return  true;
    }
}
