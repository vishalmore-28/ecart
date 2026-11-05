package com.ecart.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;

public class Totals {
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal shipping;
    private BigDecimal total;
    private String couponCode;
    private String couponMessage;

    public BigDecimal getSubtotal(){ return subtotal; }
    public void setSubtotal(BigDecimal v){ this.subtotal=v; }
    public BigDecimal getDiscount(){ return discount; }
    public void setDiscount(BigDecimal v){ this.discount=v; }
    public BigDecimal getTax(){ return tax; }
    public void setTax(BigDecimal v){ this.tax=v; }
    public BigDecimal getShipping(){ return shipping; }
    public void setShipping(BigDecimal v){ this.shipping=v; }
    public BigDecimal getTotal(){ return total; }
    public void setTotal(BigDecimal v){ this.total=v; }
    public String getCouponCode(){ return couponCode; }
    public void setCouponCode(String v){ this.couponCode=v; }
    public String getCouponMessage(){ return couponMessage; }
    public void setCouponMessage(String v){ this.couponMessage=v; }
}
