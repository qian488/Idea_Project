package com.itTest.d6_singleinstance;

public class A {
    // 定义一个类变量记住类的一个对象
    private static A a = new A();

    // 把构造器私有
    private A(){

    }

    // 定义一个类方法，返回对象
    public static A getInstance(){
        return a;
    }
}
