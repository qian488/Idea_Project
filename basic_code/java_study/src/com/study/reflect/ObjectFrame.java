package com.study.reflect;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;

public class ObjectFrame {
    public static void saveObject(Object obj) throws Exception {
        PrintStream out = new PrintStream(new FileOutputStream("object.txt",true));
        Class c = obj.getClass();
        String cName = c.getSimpleName();
        out.println("======="+cName+"=======");
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            String name = field.getName();
            field.setAccessible(true);
            String value = field.get(obj).toString();
            out.println(name+"="+value);
        }
        out.close();
    }
}
