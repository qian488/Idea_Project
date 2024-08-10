package com.itTest.demo;

import java.util.ArrayList;
import java.util.Scanner;

public class ATM {
    private ArrayList<Account> accounts = new ArrayList<>();
    private Scanner sc = new Scanner(System.in);

    public void start(){
        while (true) {
            System.out.println("====欢迎您进入ATM系统====");
            System.out.println("1.用户登录");
            System.out.println("2.用户开户");
            System.out.println("请选择:(0退出)");
            int command = sc.nextInt();
            switch (command){
                case 0:
                    System.out.println("感谢您的使用~~");
                    break;
                case 1:
                    break;
                case 2:
                    createAccount();
                    break;
                default:
                    System.out.println("没有该操作~~");
            }
        }
    }

    private void createAccount(){
        System.out.println("====系统开户====");
        Account acc = new Account();

        System.out.println("请输入您的账户名称：");
        String name = sc.next();
        acc.setUserName(name);

        while (true) {
            System.out.println("请输入您的性别：");
            char sex = sc.next().charAt(0);
            if (sex == '男' || sex == '女'){
                acc.setSex(sex);
                break;
            }else{
                System.out.println("请输入男或者女~~");
            }
        }

        while (true) {
            System.out.println("请输入您的账号密码：");
            String passWord = sc.next();
            System.out.println("请再次输入您的密码以确认：");
            String okPassWord = sc.next();
            if (okPassWord.equals(passWord)){
                acc.setPassWord(okPassWord);
                break;
            }else{
                System.out.println("密码不一致~~");
            }
        }

        System.out.println("请输入您的取现额度(上限)：");
        double limit = sc.nextDouble();
        acc.setLimit(limit);

        accounts.add(acc);
        System.out.println("恭喜你"+ acc.getUserName() + "开户完成！");
    }
}
