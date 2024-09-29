package com.itTest.d3_util;

import java.util.Random;

public class MyUtil {
    public static String CreateCode(int n){
        String code = "";
        String data = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

        Random rand = new Random();

        for (int i = 0; i < n; i++) {
            int index = rand.nextInt(data.length());
            code += data.charAt(index);
        }
        return code;
    }
}
