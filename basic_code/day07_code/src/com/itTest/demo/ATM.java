package com.itTest.demo;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class ATM {
    private ArrayList<Account> accounts = new ArrayList<>();
    private Scanner sc = new Scanner(System.in);
    private Account loginAcc;

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
                    return;
                case 1:
                    login();
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

        double limit = 50000.00d;
        System.out.println("您的取现额度(上限)为"+ String.format("%.2f", limit) );
        // double limit = sc.nextDouble();
        acc.setLimit(limit);

        String newCardId = createCardId();
        acc.setCardId(newCardId);

        accounts.add(acc);
        System.out.println("恭喜你"+ acc.getUserName() + "开户完成！" + "您的卡号为：" + acc.getCardId() );
    }

    private void login(){
        System.out.println("====系统登录====");
        if (accounts.size() == 0){
            System.out.println("系统中无任何账号，请先开户~~");
            return;
        }

        while (true) {
            System.out.println("请您输入您的卡号:");
            String cardId = sc.next();

            Account acc = getAccountByCardId(cardId);
            if (acc == null){
                System.out.println("您输入的登录卡号不存在，请确认~~");
            }else{
                while (true){
                    System.out.println("请输入您的登录密码:");
                    String passWord = sc.next();

                    if (acc.getPassWord().equals(passWord)){
                        System.out.println("恭喜您，" + acc.getUserName() + "登陆成功");
                        loginAcc = acc;
                        showUserCommand();
                        return;
                    }else{
                        System.out.println("您输入的密码不正确，请确认~~");
                    }

                }

            }

        }
    }

    private void showUserCommand(){
        while (true) {
            System.out.println("==" + loginAcc.getUserName() +"您可以进行以下操作==");
            System.out.println("1.查询账号");
            System.out.println("2.存款");
            System.out.println("3.取款");
            System.out.println("4.转账");
            System.out.println("5.密码修改");
            System.out.println("6.退出");
            System.out.println("7.注销");

            int command = sc.nextInt();
            switch (command){
                case 1:
                    showLoginAccount();
                    break;
                case 2:
                    depositMoney();
                    break;
                case 3:
                    dramMoney();
                    break;
                case 4:
                    transferMoney();
                    break;
                case 5:
                    updatePassWord();
                    break;
                case 6:
                    System.out.println(loginAcc.getUserName() + "退出系统");
                    return;
                case 7:
                    if(deleteAccount()) {
                        return;
                    }
                    break;
                default:
                    System.out.println("没有该指令~~");
            }
        }
    }

    private void updatePassWord() {
        System.out.println("==修改密码==");
        System.out.println("请输入原密码:");
        String checkPassWord = sc.next();
        if (!loginAcc.getPassWord().equals(checkPassWord)){
            System.out.println("密码错误，退出修改密码");
            return;
        }

        while (true) {
            System.out.println("请输入新密码:");
            String newPassWord = sc.next();
            System.out.println("请验证输入的新密码:");
            String okPassWord = sc.next();
            if (newPassWord.equals(okPassWord)){
                loginAcc.setPassWord(newPassWord);
                System.out.println("已更新密码");
                return;
            }else{
                System.out.println("密码不一致，请重新输入");
            }
        }
    }

    private boolean deleteAccount() {
        System.out.println("==销户==");

        while (true) {
            System.out.println("请确认是否销户【y/n】");
            String ok = sc.next();
            if (ok.equals("y") || ok.equals("Y")){
                if (loginAcc.getMoney() > 0){
                    System.out.println("账号还有余额，无法销户~~");
                    return false;
                }

                System.out.println("请输入当前账号的密码");
                String passWord = sc.next();
                if (loginAcc.getPassWord().equals(passWord)){
                    accounts.remove(loginAcc);
                    return true;
                }else{
                    System.out.println("密码不正确，取消销户");
                    return false;
                }

            }else if(ok.equals("n") || ok.equals("N")){
                System.out.println("你选择否，取消销户");
                return false;
            }else{
                System.out.println("无效指令，请输入【y/n】");
            }
        }

    }

    private void transferMoney() {
        System.out.println("==转账==");

        if(accounts.size()<2){
            System.out.println("系统中仅一个账号，无法转账~~");
            return;
        }

        if (loginAcc.getMoney()==0){
            System.out.println("余额为0，无法转账~~");
            return;
        }

        while (true) {
            System.out.println("请输入需要转入的账号卡号：");
            String cardId = sc.next();

            Account account = getAccountByCardId(cardId);
            if (account == null){
                System.out.println("您输入的卡号不存在~~");
                break;
            }

            while (true) {
                System.out.println("转入的户主为：" + account.getUserName() + "请确认【y/n】");
                String ok = sc.next();
                if(ok.equals("n") || ok.equals("N")){
                    System.out.println("你选择否，取消转账");
                    return;
                }

                if (ok.equals("y") || ok.equals("Y")){
                    System.out.println("你选择是，即将进行转账");
                    while (true) {
                        System.out.println("请输入您的账号密码：");
                        String passWord = sc.next();
                        if (loginAcc.getPassWord().equals(passWord)){
                            while (true) {
                                System.out.println("请输入需要转出的金额：");
                                double money = sc.nextDouble();
                                if (loginAcc.getMoney() > money){
                                    loginAcc.setMoney(loginAcc.getMoney() - money);
                                    account.setMoney(account.getMoney() + money);
                                    return;
                                }else{
                                    System.out.println("余额不足，无法转账。余额为：" + loginAcc.getMoney());
                                }
                            }
                        }else{
                            System.out.println("密码不正确，请重新输入");
                        }
                    }
                }

                System.out.println("无效指令，请输入【y/n】");
            }
        }
    }

    private void dramMoney() {
        System.out.println("==取款==");
        if (loginAcc.getMoney()==0){
            System.out.println("余额为0，无法取款~~");
            return;
        }

        System.out.println("请输入需要取出的金额:");
        double money = sc.nextDouble();

        if (loginAcc.getMoney() >= money){
            if (money > loginAcc.getLimit()){
                System.out.println("取款超出限额，无法取款！目前限额为：" + loginAcc.getLimit());
            }else{
                loginAcc.setMoney(loginAcc.getMoney() - money);
                System.out.println("取款" + money + " 余额为:" + loginAcc.getMoney());

                loginAcc.setLimit(loginAcc.getLimit() - money);
            }
        }else{
            System.out.println("余额不足，无法取款！账户余额为：" + loginAcc.getMoney());
        }
    }

    private void depositMoney() {
        System.out.println("==存款==");
        System.out.println("请您输入存入的金额:");
        double money = sc.nextDouble();

        loginAcc.setMoney(loginAcc.getMoney() + money);
        System.out.println("已存入" + money + " 余额为：" + loginAcc.getMoney());
    }

    private void showLoginAccount(){
        System.out.println("==当前账号信息如下==");
        System.out.println("卡号:" + loginAcc.getCardId());
        System.out.println("户主:" + loginAcc.getUserName());
        System.out.println("性别:" + loginAcc.getSex());
        System.out.println("余额:" + loginAcc.getMoney());
        System.out.println("限额:(" + loginAcc.getLimit() + "/50000.00");
    }

    /*8位数字的卡号*/
    private String createCardId(){
        while (true) {
            String cardId = "";
            Random r = new Random();
            for (int i=0;i<8;i++){
                int date = r.nextInt(10);
                cardId += date;
            }
            Account acc = getAccountByCardId(cardId);
            if (acc==null){
                return cardId;
            }
        }
    }

    private Account getAccountByCardId(String cardId){
        for (int i=0;i<accounts.size();i++){
            Account account = accounts.get(i);
            if (account.getCardId().equals(cardId)){
                return account;
            }
        }
        return null;
    }
}
