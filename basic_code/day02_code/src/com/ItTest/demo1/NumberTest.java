package com.ItTest.demo1;

public class NumberTest {
    public static void main(String[] args) {
        NumberTest.IntegerTest();
        NumberTest.MathTest();

    }
    public static void IntegerTest()
    {
        Integer x = 5;
        x =  x + 10;
        System.out.println(x);
    }
    public  static  void MathTest()
    {
        System.out.println("90 度的正弦值：" + Math.sin(Math.PI/2));
        System.out.println("0度的余弦值：" + Math.cos(0));
        System.out.println("60度的正切值：" + Math.tan(Math.PI/3));
        System.out.println("1的反正切值： " + Math.atan(1));
        System.out.println("π/2的角度值：" + Math.toDegrees(Math.PI/2));
        System.out.println(Math.PI);
    }
}
