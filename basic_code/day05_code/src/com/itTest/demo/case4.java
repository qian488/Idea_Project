package com.itTest.demo;

public class case4 {
    public static void main(String[] args) {
        //打印三角形案例
        /**
         *       *
         *      * *
         *     * * *
         *    * * * *
         *
         *
         * 本质：计算机只能打印行，所以要按行思考
         * 先找规律，再写
         * 行（i）    空格（n - i）   星星   换行
         * 1           3             1
         * 2           2             3
         * 3           1             5
         * 4           0             7
         *
         * */
        printTriangle(10);
    }

    public static void printTriangle(int n)
    {
        //控制行数
        for (int i = 1; i <= n ; i++) {
            //打印空格
            for (int j = 1; j <= (n-i); j++) {
                System.out.print(" ");
            }
            //打印星星
            for (int j = 1; j <= (2*i-1); j++) {
                System.out.print(j % 2 == 0 ? " ":"*");
            }
            //换行
            System.out.println();
        }
    }
}
