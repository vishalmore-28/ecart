package com.ecart.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.servlet.http.HttpSession;

/** Password hashing (PBKDF2-SHA256), CSRF tokens and input validators. */
public class Security {
    private static final SecureRandom RND = new SecureRandom();
    private static final int ITER = 120000;

    public static String hash(String password) {
        byte[] salt = new byte[16];
        RND.nextBytes(salt);
        return ITER + ":" + Base64.getEncoder().encodeToString(salt) + ":"
                + Base64.getEncoder().encodeToString(pbkdf2(password.toCharArray(), salt, ITER));
    }

    public static boolean verify(String password, String stored) {
        try {
            String[] p = stored.split(":");
            byte[] expected = Base64.getDecoder().decode(p[2]);
            byte[] actual = pbkdf2(password.toCharArray(), Base64.getDecoder().decode(p[1]), Integer.parseInt(p[0]));
            return MessageDigest.isEqual(expected, actual);
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] pbkdf2(char[] pw, byte[] salt, int iter) {
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(new PBEKeySpec(pw, salt, iter, 256)).getEncoded();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String token(int bytes) {
        byte[] b = new byte[bytes];
        RND.nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }

    public static String csrf(HttpSession s) {
        String t = (String) s.getAttribute("csrf");
        if (t == null) { t = token(24); s.setAttribute("csrf", t); }
        return t;
    }

    public static boolean strongPassword(String p) {
        return p != null && p.length() >= 8 && p.length() <= 100 && p.matches(".*[A-Za-z].*") && p.matches(".*\\d.*");
    }

    public static boolean validEmail(String e) {
        return e != null && e.length() <= 150 && e.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    }

    public static boolean validPhone(String p) { return p != null && p.matches("^[6-9]\\d{9}$"); }

    public static boolean validPin(String p) { return p != null && p.matches("^\\d{6}$"); }

    /** Luhn check for the (demo) card payment form. */
    public static boolean luhn(String num) {
        if (num == null || !num.matches("\\d{13,19}")) return false;
        int sum = 0; boolean alt = false;
        for (int i = num.length() - 1; i >= 0; i--) {
            int d = num.charAt(i) - '0';
            if (alt) { d *= 2; if (d > 9) d -= 9; }
            sum += d; alt = !alt;
        }
        return sum % 10 == 0;
    }
}
