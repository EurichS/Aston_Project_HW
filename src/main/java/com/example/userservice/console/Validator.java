package com.example.userservice.console;

import java.util.regex.Pattern;

public class Validator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public static boolean isValidEmail(String email) {
        return email == null || !EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidAge(Integer age) {
        return age != null && age >= 0;
    }

    public static boolean isValidName(String name) {
        return name == null || name.trim().isEmpty();
    }

    public static boolean isValidId(Long id) {
        return id != null && id > 0;
    }
}