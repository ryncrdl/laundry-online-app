package com.example.onlinelaundry.Utils;

import android.util.Patterns;

import java.util.regex.Pattern;

public class Validation {

    public static boolean isValidEmail(String email) {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        return emailPattern.matcher(email).matches();
    }

    public static boolean isValidContact(String contact) {
        // Validate contact number in the format 09xxxxxxxxx
        Pattern contactPattern = Pattern.compile("^09\\d{9}$");
        return contactPattern.matcher(contact).matches();
    }
}
