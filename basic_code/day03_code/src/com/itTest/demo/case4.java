package com.itTest.demo;

public class case4 {
    public static void main(String[] args) {
        //数字加密案例
        //加密规则：对四位数的密码，每位数+5再%10，最后反转
        System.out.println("加密后的结果是" + encrypt(1983));
    }
    public static String encrypt(int number)
    {
        //1.将密码拆分成一个一个数字
        int[] numbers = split(number);
        //2.遍历数组，对其加密
        for (int i = 0; i <numbers.length ; i++) {
            numbers[i] = (numbers[i] + 5) % 10;
        }
        //3.反转数组，顺着输出
        reverse(numbers);
        //4.拼接加密的数字
        String data = "";
        for (int i = 0; i <numbers.length ; i++) {
            data += numbers[i];
        }
        return data;
    }

    public static void reverse(int[] numbers) {
        //反转
        for (int i = 0,j = numbers.length-1; i <j ; i++,j--) {
            //交换i和j的值
            int temp = numbers[j];
            numbers[j] = numbers[i];
            numbers[i] = temp;
        }
    }

    public static int[] split(int number) {
        //拆分密码
        int[] numbers = new int[4];
        numbers[0] = number / 1000;
        numbers[1] = (number / 100) % 10;
        numbers[2] = (number / 10) % 10;
        numbers[3] = number % 10;
        return  numbers;
    }
}
