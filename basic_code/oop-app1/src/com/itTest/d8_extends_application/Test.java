package com.itTest.d8_extends_application;

public class Test {
    public static void main(String[] args) {
        Teacher teacher = new Teacher();
        teacher.setName("六花");
        teacher.setSkill("python");
        System.out.println(teacher.getName());
        System.out.println(teacher.getSkill());
        teacher.PrintInfo();
    }
}
