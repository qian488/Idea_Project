package com.study.proxy;

import org.junit.Test;

public class ProxyTest {
    @Test
    public void testGmaing() {
        Gaming gm = ProxyUtil.createProxy(new Gamingmpl());

        System.out.println("========================");
        gm.jump("admin",10);
        System.out.println("========================");
        gm.move("rikka",10);
        System.out.println("========================");
        gm.attack("steve",5);
        System.out.println("========================");
        String[] errors = gm.SendError();
        for (String error : errors) {
            System.out.println(error);
        }

    }
}
