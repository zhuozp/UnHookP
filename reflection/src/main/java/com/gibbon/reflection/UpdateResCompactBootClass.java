package com.gibbon.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static android.os.Build.VERSION.SDK_INT;

/**
 * Created by zhipengzhuo on 2021/8/13 14:29
 */
public class UpdateResCompactBootClass {

    public static Method sGetDeclaredFieldMethod;
    public static Class<?> sStyleableClz;

    static {
        if (SDK_INT >= 30) {
            try {
                Method forName = Class.class.getDeclaredMethod("forName", String.class);
                sGetDeclaredFieldMethod = Class.class.getDeclaredMethod("getDeclaredField", String.class);
                sStyleableClz = (Class<?>) forName.invoke(null, "com.android.internal.R$styleable");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Object reflect(String name) {
        if (sGetDeclaredFieldMethod == null || sStyleableClz == null) {
            return null;
        }
        try {
            Field field = (Field) sGetDeclaredFieldMethod.invoke(sStyleableClz, name);
            field.setAccessible(true);
            return field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
