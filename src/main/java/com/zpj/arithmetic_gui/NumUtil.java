package com.zpj.arithmetic_gui;

import java.util.regex.Pattern;

/**
 * 数字工具类
 * @author Z-P-J
 */
public class NumUtil {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("^[-\\+]?[\\d]*$");

    private NumUtil() {

    }

    /**
     * 检测字符串是否是数字字符串
     * @param str 待测字符串
     * @return 是（true） or 否(false)
     */
    public static boolean isInteger(String str) {
        return NUMBER_PATTERN.matcher(str).matches();
    }

    public static int countBlanks(String str) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == 32) {
                count++;
            }
        }
        return count;
    }


}
