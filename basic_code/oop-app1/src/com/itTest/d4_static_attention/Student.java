package com.itTest.d4_static_attention;

public class Student {
    static  String schoolName;
    double score;

    public static void PrintHelloWorld(){
        schoolName="school";
        PrintHelloSchool();


        // 类方法不能访问实例变量
        // System.out.println(score);
        // PrintPass();
        // System.out.println(this);
    }

    public static void PrintHelloSchool(){
        System.out.printf("Hello school!");
    }

    public void PrintPass(){
        schoolName="DGUT";
        PrintPass();

        System.out.println(score);

        PrintPass2();
    }

    public void PrintPass2(){
        System.out.println(this.score);
    }
}
