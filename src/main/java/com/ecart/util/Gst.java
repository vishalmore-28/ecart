package com.ecart.util;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Indian GST / seller-onboarding helpers: GSTIN (with check digit), PAN, IFSC, state codes, GST slabs. */
public final class Gst {
    private Gst() { }

    /** GST state code -> state / UT name (first two digits of a GSTIN). */
    public static final Map<String, String> STATES = new LinkedHashMap<>();
    static {
        String[] s = {
            "01","Jammu and Kashmir","02","Himachal Pradesh","03","Punjab","04","Chandigarh","05","Uttarakhand","06","Haryana",
            "07","Delhi","08","Rajasthan","09","Uttar Pradesh","10","Bihar","11","Sikkim","12","Arunachal Pradesh","13","Nagaland",
            "14","Manipur","15","Mizoram","16","Tripura","17","Meghalaya","18","Assam","19","West Bengal","20","Jharkhand",
            "21","Odisha","22","Chhattisgarh","23","Madhya Pradesh","24","Gujarat","26","Dadra and Nagar Haveli and Daman and Diu",
            "27","Maharashtra","29","Karnataka","30","Goa","31","Lakshadweep","32","Kerala","33","Tamil Nadu","34","Puducherry",
            "35","Andaman and Nicobar Islands","36","Telangana","37","Andhra Pradesh","38","Ladakh"
        };
        for (int i = 0; i < s.length; i += 2) STATES.put(s[i], s[i + 1]);
    }

    /** Standard GST slabs for goods. */
    public static final List<BigDecimal> SLABS = Arrays.asList(
            new BigDecimal("0"), new BigDecimal("5"), new BigDecimal("12"), new BigDecimal("18"), new BigDecimal("28"));

    public static final List<String> BUSINESS_TYPES = Arrays.asList(
            "Proprietorship", "Partnership Firm", "LLP", "Private Limited", "Public Limited", "HUF");

    private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String stateOfCode(String code) { return STATES.get(code); }

    public static boolean validPan(String pan) { return pan != null && pan.matches("^[A-Z]{5}[0-9]{4}[A-Z]$"); }

    public static boolean validIfsc(String ifsc) { return ifsc != null && ifsc.matches("^[A-Z]{4}0[A-Z0-9]{6}$"); }

    public static boolean validHsn(String hsn) { return hsn != null && hsn.matches("^\\d{4}(\\d{2}|\\d{4})?$"); }

    /** Format, known state code and the mod-36 check character. */
    public static boolean validGstin(String g) {
        if (g == null || !g.matches("^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z][1-9A-Z]Z[0-9A-Z]$")) return false;
        if (!STATES.containsKey(g.substring(0, 2))) return false;
        return g.charAt(14) == checkChar(g.substring(0, 14));
    }

    static char checkChar(String first14) {
        int sum = 0;
        for (int i = 0; i < 14; i++) {
            int v = CHARS.indexOf(first14.charAt(i));
            int p = v * (i % 2 == 0 ? 1 : 2);
            sum += p / 36 + p % 36;
        }
        return CHARS.charAt((36 - (sum % 36)) % 36);
    }

    /** The PAN embedded in a GSTIN (characters 3 to 12). */
    public static String panOf(String gstin) { return gstin.substring(2, 12); }

    /** 4th letter of a PAN tells the holder type: P person, C company, F firm/LLP, H HUF. */
    public static boolean panMatchesBusiness(String pan, String businessType) {
        char t = pan.charAt(3);
        switch (businessType) {
            case "Proprietorship": return t == 'P';
            case "Partnership Firm": case "LLP": return t == 'F';
            case "Private Limited": case "Public Limited": return t == 'C';
            case "HUF": return t == 'H';
            default: return false;
        }
    }

    public static boolean validSlab(BigDecimal r) {
        if (r == null) return false;
        for (BigDecimal s : SLABS) if (s.compareTo(r) == 0) return true;
        return false;
    }
}
