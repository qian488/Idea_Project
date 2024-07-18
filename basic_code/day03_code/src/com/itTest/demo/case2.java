package com.itTest.demo;

import java.util.Random;

public class case2 {
    public static void main(String[] args) {
        //验证码案例
        //随机生成指定位数的验证码
        System.out.println(case2.createCode(5));
    }

    public static  String createCode(int n)
    {
        //1.定义一个for循环用于控制产生多少位随机字符
        Random r = new Random();
        //2.定义一个String类型变量储存每位随机数字符
        String code ="";
        for (int i=0;i<n;i++)
        {
            //i = 0 1 2 3 4
            //每个位置可能是数字，大小写字母
            //可以分为三类 0-->数字 1-->大写字母 2-->小写字母
            int type =r.nextInt(3);
            switch (type)
            {
                case 0:
                    //随机一个0-9的数字
                    code += r.nextInt(10);
                    break;
                case 1:
                    //随机一个大写字母A--Z
                    char ch1 = (char) (r.nextInt(26)+65);
                    code += ch1;
                    break;
                case 2:
                    //随机一个小写字母a--z
                    char ch2 = (char) (r.nextInt(26)+97);
                    code += ch2;
                    break;
            }
        }
        return code;
    }
}
