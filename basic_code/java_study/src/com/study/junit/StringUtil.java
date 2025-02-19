package com.study.junit;

public class StringUtil {
    public static void printNumber(String str) {
        System.out.println(str + "的长度为" + str.length());
    }

    public static int GetMAxIndex(String str) {
        if (str == null || str.length() == 0) {
            return -1;
        }
        return str.length();
    }
}
