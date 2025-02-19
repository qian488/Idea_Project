package com.study.annotation.parse;

@MyAnnotation(value = "Hoshino",id = 2)
public class Demo {
    @MyAnnotation(value = "Rikka",id = 1)
    public void sayHello() {
        System.out.println("hello");
    }
}
