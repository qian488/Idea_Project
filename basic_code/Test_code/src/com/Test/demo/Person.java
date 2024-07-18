package com.Test.demo;

public class Person {
    String name;
    int age = 25;

    public Person(String name) {
        //this(); // Line n1
        this.name = name;
    }

    public Person(String name, int age) {
        this(name); // Line n2
        this.age = age;
    }

    public String show() {
        return name + " " + age;
    }

    public static void main(String[] args) {
        Person p1 = new Person("Jesse");
        Person p2 = new Person("Walter", 52);
        System.out.println(p1.show());
        System.out.println(p2.show());
    }
}
