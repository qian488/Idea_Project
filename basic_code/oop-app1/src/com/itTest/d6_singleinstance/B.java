package com.itTest.d6_singleinstance;

public class B {
    private static B b;

    private B() {

    }
    // 第一次调用才创建对象
    public static B getInstance() {
        if (b == null) {
            b = new B();
        }
        return b;
    }
}
