package com.study.annotation.demo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JunitLike {
    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
        JunitLike junitLike = new JunitLike();
        Class c = JunitLike.class;
        Method[] methods = c.getDeclaredMethods();
        for (Method method : methods) {
            if(method.isAnnotationPresent(MyTest.class)) {
                method.invoke(junitLike);
            }
        }
    }

    @MyTest
    public void test1() {
        System.out.println("=====test1======");
    }

    public void test2() {
        System.out.println("=====test2======");
    }

    @MyTest
    public void test3() {
        System.out.println("=====test3======");
    }

    public void test4() {
        System.out.println("=====test4======");
    }
}
