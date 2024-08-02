package uz.pdp.utils;

import java.util.regex.Pattern;

public interface Utils {
    static boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
        return pattern.matcher(email).matches();
    }

    static boolean isValidPassword(String password) {
        return password.length() >= 6;
    }
}
