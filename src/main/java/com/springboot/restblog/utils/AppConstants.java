package com.springboot.restblog.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppConstants {
    public static final String PAGE_NUMBER = "0";
    public static final String PAGE_SIZE = "5";
    public static final String SORT_BY = "id";
    public static final String SORT_DIRECTION = "asc";
    public static final Pattern VALID_EMAIL
            = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static boolean validate(String email) {
        Matcher matcher = VALID_EMAIL.matcher(email);
        return matcher.find();
    }
}
