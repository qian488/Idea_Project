package com.itTest.d12_extends_override;

public class Test {
    public static void main(String[] args) {
        B b = new B();
        b.Print1();
        b.Print2(2,2);
        System.out.println("=================================");

        Student s = new Student("cheng",18);
        System.out.println(s.toString());
        System.out.println(s);
    }
}
