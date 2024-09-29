package com.itTest.d7_extends;

import com.itTest.d6_singleinstance.B;

public class Test {
    public static void main(String[] args) {
        Children b = new Children();
        System.out.println(b.i);

        b.Print1();

        b.Print3();
    }
}
