package com.itTest.demo;

public class case1 {
    public static void main(String[] args) {
        //找素数2
        System.out.println("当前素数的个数是" + search(101, 200));
    }

    public static int search(int start,int end)
    {
        int count = 0;

        OUT://为外部循环指定标签
        for (int i = start; i <= end; i++) {

            for (int j = 2; j <= i/2; j++) {
                if(i % j == 0)
                {
                    continue OUT;
                }
            }
            count++;
            System.out.println(i);
        }
        return count;
    }
}
