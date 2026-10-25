package com.Util;

import java.util.ArrayList;

import com.Bean.ProductBean;

public final class ProductUtil {

    // stock at or below this number is shown as "Low stock"
    public static final int LOW_STOCK = 5;

    private ProductUtil() {
    }

    public static int qty(String s) {
        try {
            return (int) Double.parseDouble(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public static double price(String s) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private static boolean has(String text, String key) {
        return text != null && text.toLowerCase().contains(key);
    }

    // Returns the products whose code, name or company contains the search text
    public static ArrayList<ProductBean> filter(ArrayList<ProductBean> list, String q) {
        if (list == null || q == null || q.trim().isEmpty()) {
            return list;
        }
        String key = q.trim().toLowerCase();
        ArrayList<ProductBean> result = new ArrayList<>();
        for (ProductBean p : list) {
            if (has(p.getPcode(), key) || has(p.getPname(), key) || has(p.getPcompany(), key)) {
                result.add(p);
            }
        }
        return result;
    }
}
