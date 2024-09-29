package com.itTest.d2_method;

public class Student {
    double score;

    public static void PrintHelloWorld() {
        System.out.println("Hello World");
        System.out.println("Hello World");
    }

    public  void PrintPass(){
        System.out.println("成绩:"+(score >= 60 ? "pass" : "no pass"));
    }
}
