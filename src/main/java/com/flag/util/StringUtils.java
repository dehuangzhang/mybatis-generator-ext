package com.flag.util;

/**
 * Created by wb-zdh274635 on 2017/9/22.
 */
public class StringUtils {

    private static final String UNDERLINE = "_";

    /**
     * 将表名转成驼峰结构
     * 
     * @param tableName
     * @return
     */
    public static String toHumpStructure(String tableName) {
        if (tableName == null) {
            return "";
        }
        if (!tableName.contains(UNDERLINE)) {
            return tableName;
        }
        String[] array = tableName.split("_");
        StringBuilder builder = new StringBuilder();
        for (String str : array) {
            char[] cs = str.toCharArray();
            cs[0] -= 32;
            builder.append(String.valueOf(cs));
        }
        return builder.toString();
    }

    /**
     * str是否为空字符串或者 null
     * 
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        if (str == null || str.trim() == "") {
            return true;
        }
        return false;
    }
}
