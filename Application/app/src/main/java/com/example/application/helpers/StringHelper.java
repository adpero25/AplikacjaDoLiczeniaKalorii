package com.example.application.helpers;

import android.text.TextUtils;

public class StringHelper {
    public static boolean checkNullOrEmpty(String s) {
        return s != null && !TextUtils.isEmpty(s);
    }
    public static boolean checkPositiveNumber(String s){return Double.parseDouble(s) >= 0;}

}
