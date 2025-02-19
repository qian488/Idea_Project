package com.study.reflect;

public class Test {
    public static void main(String[] args) throws Exception {
        Student student = new Student("张三",18,"男");
        Teacher teacher = new Teacher("落叶",24,"女","广州","188-5674-8499");
        ObjectFrame.saveObject(student);
        ObjectFrame.saveObject(teacher);
        System.out.println("====请在项目根目录下找到object.txt查看====");
    }
}
