package com.itTest.d6_singleinstance;

public class Test1 {
    public static void main(String[] args) {
        A a1 = A.getInstance();
        A a2 = A.getInstance();
        System.out.println(a1);
        System.out.println(a2);
        System.out.println(a1==a2);
    }
}
