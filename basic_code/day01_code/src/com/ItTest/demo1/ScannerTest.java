package com.ItTest.demo1;
import java.util.Scanner;
public class ScannerTest {
    public static void main(String[] args){
        Scanner scan=new Scanner(System.in);
        System.out.println("请输入：");
        String str1=scan.next();
        System.out.println("输入的数据为"+str1);
    }
}
