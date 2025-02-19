package com.study.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyUtil {
    public static Gaming createProxy(Gaming gaming) {
        Gaming gamingProxy = (Gaming) Proxy.newProxyInstance(
                ProxyUtil.class.getClassLoader(),
                new Class[]{Gaming.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if(method.getName().equals("jump")) {
                            System.out.println("jumpjumpjump");
                        }else if(method.getName().equals("move")) {
                            System.out.println("movemovemove");
                        }else if(method.getName().equals("attack")) {
                            System.out.println("attackattackattack");
                        }else{
                            System.out.println("ErrorErrorError");
                        }
                        return method.invoke(gaming, args);
                    }
                }
        );
        return gamingProxy;
    }
}
