package com.study.annotation.parse;
// 反编译工具 Xjad

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

public class MyAnnotationTest {
    @Test
    public void parseClass(){
        Class c = Demo.class;
        if(c.isAnnotationPresent(MyAnnotation.class)){
            MyAnnotation myAnnotation = (MyAnnotation) c.getDeclaredAnnotation(MyAnnotation.class);
            System.out.println(myAnnotation.value());
            System.out.println(myAnnotation.id());
        }
    }

    @Test
    public void parseMethod() throws NoSuchMethodException {
        Method m = Demo.class.getDeclaredMethod("sayHello");
        if(m.isAnnotationPresent(MyAnnotation.class)){
            MyAnnotation myAnnotation = (MyAnnotation) m.getDeclaredAnnotation(MyAnnotation.class);
            System.out.println(myAnnotation.value());
            System.out.println(myAnnotation.id());
        }
    }
}
