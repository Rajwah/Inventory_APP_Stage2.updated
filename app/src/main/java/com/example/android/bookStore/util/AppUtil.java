package com.example.android.bookStore.util;

/**
 * This is the utility class for app, contains utility methods
 */
public class AppUtil {

    public static boolean getNullCheck(String str) {
        if (str == null || str.length() == 0 || str.equalsIgnoreCase("null")) {
            return false;
        }
        return true;
    }

    public static boolean getNullCheck(Object obj) {
        if (obj == null) {
            return false;
        }
        return true;
    }
}
