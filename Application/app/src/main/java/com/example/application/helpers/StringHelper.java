package com.example.application.helpers;

import android.text.TextUtils;

public class StringHelper {
    public static boolean checkNullOrEmpty(String s) {
        return s != null && !TextUtils.isEmpty(s);
    }
}
