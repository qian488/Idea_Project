package com.itTest.d5_block;

public class Student {
    static int number = 80;
    static String name;

    // 静态代码块
    static {
        System.out.println("静态代码块执行~~");
        name="Jack";
    }

    int age;
    // 实例代码块
    {
        System.out.println("实例代码块~~");
        // age=10; 使用较少、意义不大
        System.out.println("创建了对象" + this); // 实例代码块在构造器之前执行，可以用来记录日志，减少构造器中的代码重复
    }

    public Student() {
        System.out.println("无参构造器");
    }
    public Student(String name) {
        System.out.println("有参构造器");
    }
}
