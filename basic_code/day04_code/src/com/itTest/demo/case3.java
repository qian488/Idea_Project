package com.itTest.demo;

public class case3 {
    public static void main(String[] args) {
        //找素数案例
        System.out.println("当前素数的个数是" + search(101, 200));
    }

    public static int search(int start,int end)
    {
        int count = 0;
        //1.定义一个for循环找到之间的每个数据
        for (int i = start; i <= end ; i++) {
            boolean flag = true;
            //2.判断是否是素数
            for (int j = 2; j < i/2 ; j++)
            {
                if (i % j == 0)
                {
                    flag = false;
                    break;
                }
            }
            //3.输出素数
            if (flag == true) {
                System.out.println(i);
                count++;
            }
        }
        return count;
    }
}
