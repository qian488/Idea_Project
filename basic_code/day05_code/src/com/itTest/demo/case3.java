package com.itTest.demo;

public class case3 {
    public static void main(String[] args) {
        //99乘法表案例
        printTable();
    }

    public static void printTable()
    {
        //1.外循环控制打印多少行
        for (int i = 1; i <= 9; i++) {
            //2.内循环控制打印列
            for (int j = 1; j <= i ; j++) {
                System.out.print(j + "x" + i + "=" + (j*i)+"\t");
            }
            System.out.println();
        }
    }
}
