package com.itTest.d1_staticdemo;

public class Test {
    public static void main(String[] args) {
        Student.name = "极限空间";

        Student s1 = new Student();
        s1.name = "s1";
        Student s2 = new Student();
        s2.name = "s2";
        // 对象访问类变量，会有警告，但是不影响访问
        System.out.println(s1.name);
        System.out.println(Student.name);

        s1.age = 11;
        s2.age = 12;

        System.out.println(s1.age);
        System.out.println(s2.age);
    }
}
