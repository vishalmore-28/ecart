package com.ecart.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;

public class OrderItem {
    private int id;
    private int orderId;
    private int productId;
    private String name;
    private BigDecimal price;
    private int qty;
    private int sellerId;
    private String sellerName;
    private BigDecimal gstPercent = new BigDecimal("18");
    private String hsnCode;
    private String itemStatus = "PLACED";
    private String courier;
    private String trackingNo;
    private BigDecimal taxable;
    private BigDecimal tax;

    public int getId(){ return id; }
    public void setId(int v){ this.id=v; }
    public int getOrderId(){ return orderId; }
    public void setOrderId(int v){ this.orderId=v; }
    public int getProductId(){ return productId; }
    public void setProductId(int v){ this.productId=v; }
    public String getName(){ return name; }
    public void setName(String v){ this.name=v; }
    public BigDecimal getPrice(){ return price; }
    public void setPrice(BigDecimal v){ this.price=v; }
    public int getQty(){ return qty; }
    public void setQty(int v){ this.qty=v; }
    public int getSellerId(){ return sellerId; }
    public void setSellerId(int v){ this.sellerId=v; }
    public String getSellerName(){ return sellerName; }
    public void setSellerName(String v){ this.sellerName=v; }
    public BigDecimal getGstPercent(){ return gstPercent; }
    public void setGstPercent(BigDecimal v){ this.gstPercent=v; }
    public String getHsnCode(){ return hsnCode; }
    public void setHsnCode(String v){ this.hsnCode=v; }
    public String getItemStatus(){ return itemStatus; }
    public void setItemStatus(String v){ this.itemStatus=v; }
    public String getCourier(){ return courier; }
    public void setCourier(String v){ this.courier=v; }
    public String getTrackingNo(){ return trackingNo; }
    public void setTrackingNo(String v){ this.trackingNo=v; }
    /** Taxable value after the coupon share and GST on it (filled in by the seller order view). */
    public BigDecimal getTaxable(){ return taxable; }
    public void setTaxable(BigDecimal v){ this.taxable=v; }
    public BigDecimal getTax(){ return tax; }
    public void setTax(BigDecimal v){ this.tax=v; }
    public BigDecimal getLineTotal(){ return price.multiply(BigDecimal.valueOf(qty)); }
}
