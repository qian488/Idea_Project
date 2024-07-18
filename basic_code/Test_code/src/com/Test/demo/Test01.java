package com.Test.demo;


import java.util.ArrayList;
import java.util.List;

public class Test01 {
    public static void main(String[] args) {
        Base base = new Base();
        base.print(new Son());
        Sub sub = new Sub();
        sub.print(new Son());
    }

    static class  Base{
        public void print(Father arrayList){
            System.out.println("这是父类执行过程");
        }
    }

    static class Sub extends Base{
        public void print(Son list){
            System.out.println("这是子类执行过程");
        }
    }

    static class Father{

    }

    static class Son extends Father{

    }
}
