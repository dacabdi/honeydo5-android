package com.honeydo5.honeydo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidation {
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String USERNAME_PATTERN = "[a-zA-Z0-9\\._\\-]{3,}"; //does not allow spaces

    public static boolean validateEmail(String email){
        return match(EMAIL_PATTERN, email);
    }

    public static boolean validateUsername(String username){
        return match(USERNAME_PATTERN, username);
    }

    public static boolean validateDate(String date) {
        return true;
        //TODO: David add a date regex
    }

    public static boolean validateTime(String time) {
        return true;
        //TODO: David add a time regex
    }

    public static boolean checkIfEmpty(String str) {
        return str.isEmpty();
    }

    private static boolean match(String strPattern, String str){
        Pattern pattern = Pattern.compile(strPattern);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
}
