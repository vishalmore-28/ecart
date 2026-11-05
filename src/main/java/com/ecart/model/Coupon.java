package com.ecart.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;

public class Coupon {
    private String code;
    private int percent;
    private BigDecimal minOrder;
    private BigDecimal maxDiscount;
    private boolean active;

    public String getCode(){ return code; }
    public void setCode(String v){ this.code=v; }
    public int getPercent(){ return percent; }
    public void setPercent(int v){ this.percent=v; }
    public BigDecimal getMinOrder(){ return minOrder; }
    public void setMinOrder(BigDecimal v){ this.minOrder=v; }
    public BigDecimal getMaxDiscount(){ return maxDiscount; }
    public void setMaxDiscount(BigDecimal v){ this.maxDiscount=v; }
    public boolean isActive(){ return active; }
    public void setActive(boolean v){ this.active=v; }
}
