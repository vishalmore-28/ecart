package com.ecart.util;

import com.ecart.model.CartItem;
import com.ecart.model.Coupon;
import com.ecart.model.Totals;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/** All money maths lives here so cart, checkout and order placement always agree. */
public class PriceCalc {
    public static final BigDecimal GST_PERCENT = new BigDecimal("18");
    public static final BigDecimal FREE_SHIPPING_ABOVE = new BigDecimal("5000");
    public static final BigDecimal SHIPPING_FEE = new BigDecimal("99");
    public static final BigDecimal COD_LIMIT = new BigDecimal("50000");
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    /** Coupon discount is shared between items in proportion to their value. */
    public static BigDecimal itemTaxable(BigDecimal line, BigDecimal subtotal, BigDecimal discount) {
        if (subtotal == null || subtotal.signum() <= 0 || discount == null || discount.signum() <= 0) return line;
        BigDecimal share = discount.multiply(line).divide(subtotal, 4, RoundingMode.HALF_UP);
        return line.subtract(share);
    }

    /** GST on one order line at that product's own rate (0, 5, 12, 18 or 28 percent). */
    public static BigDecimal itemTax(BigDecimal line, BigDecimal subtotal, BigDecimal discount, BigDecimal gstPercent) {
        BigDecimal rate = gstPercent == null ? GST_PERCENT : gstPercent;
        return itemTaxable(line, subtotal, discount).multiply(rate).divide(HUNDRED, 2, RoundingMode.HALF_UP);
    }

    public static Totals compute(List<CartItem> items, Coupon coupon) {
        BigDecimal sub = BigDecimal.ZERO;
        for (CartItem ci : items) sub = sub.add(ci.getLineTotal());
        Totals t = new Totals();
        t.setSubtotal(sub);
        BigDecimal disc = BigDecimal.ZERO;
        if (coupon != null && coupon.isActive() && sub.signum() > 0) {
            if (sub.compareTo(coupon.getMinOrder()) >= 0) {
                disc = sub.multiply(BigDecimal.valueOf(coupon.getPercent())).divide(HUNDRED, 2, RoundingMode.HALF_UP);
                BigDecimal max = coupon.getMaxDiscount();
                if (max != null && max.signum() > 0 && disc.compareTo(max) > 0) disc = max;
                t.setCouponCode(coupon.getCode());
            } else {
                t.setCouponMessage("Coupon " + coupon.getCode() + " needs a minimum order of \u20B9"
                        + coupon.getMinOrder().setScale(0, RoundingMode.HALF_UP));
            }
        }
        BigDecimal taxable = sub.subtract(disc);
        t.setDiscount(disc);
        BigDecimal tax = BigDecimal.ZERO;
        for (CartItem ci : items) tax = tax.add(itemTax(ci.getLineTotal(), sub, disc, ci.getProduct().getGstPercent()));
        t.setTax(tax);
        t.setShipping(sub.signum() == 0 || taxable.compareTo(FREE_SHIPPING_ABOVE) >= 0 ? BigDecimal.ZERO : SHIPPING_FEE);
        t.setTotal(taxable.add(t.getTax()).add(t.getShipping()));
        return t;
    }
}
