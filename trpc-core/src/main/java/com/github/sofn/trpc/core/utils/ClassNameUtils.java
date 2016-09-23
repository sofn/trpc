package com.github.sofn.trpc.core.utils;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-09-23 16:06
 */
public class ClassNameUtils {

    public static String getClassName(Class clazz) {
        return clazz.getName();
    }

    public static String getOuterClassName(Class clazz) {
        int spliterIndex = clazz.getName().lastIndexOf("$");
        return spliterIndex > 0 ? clazz.getName().substring(0, spliterIndex) : clazz.getName();
    }
}
