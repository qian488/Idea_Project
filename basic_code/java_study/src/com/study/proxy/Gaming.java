package com.study.proxy;

public interface Gaming {
    void jump(Object obj, int distance);
    void move(Object obj, int distance);
    void attack(Object obj, int power);
    String[] SendError();
}
