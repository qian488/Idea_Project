package com.study.proxy;

public class Gamingmpl implements Gaming {
    @Override
    public String[] SendError() {
        String[] errors = new String[5];
        for (int i = 0; i < errors.length; i++) {
            errors[i] = "error" + (i + 1);
        }
        return errors;
    }

    @Override
    public void jump(Object obj, int distance){
        System.out.println(obj.toString() + " jump " + distance);
    }

    @Override
    public void move(Object obj, int distance){
        System.out.println(obj.toString() + " move " + distance * 2);
    }

    @Override
    public void attack(Object obj, int power){
        System.out.println(obj.toString() + " attack " + power * 3);
    }
}
