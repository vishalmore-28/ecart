package com.ecart.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;

public class Product {
    private int id;
    private int categoryId;
    private String categoryName;
    private String icon;
    private String name;
    private String brand;
    private String description;
    private BigDecimal price;
    private BigDecimal mrp;
    private int stock;
    private String imageUrl;
    private boolean active;
    private double avgRating;
    private int reviewCount;
    private int sellerId;
    private String sellerName;
    private String sellerStatus;
    private BigDecimal gstPercent = new BigDecimal("18");
    private String hsnCode;

    public int getId(){ return id; }
    public void setId(int v){ this.id=v; }
    public int getCategoryId(){ return categoryId; }
    public void setCategoryId(int v){ this.categoryId=v; }
    public String getCategoryName(){ return categoryName; }
    public void setCategoryName(String v){ this.categoryName=v; }
    public String getIcon(){ return icon; }
    public void setIcon(String v){ this.icon=v; }
    public String getName(){ return name; }
    public void setName(String v){ this.name=v; }
    public String getBrand(){ return brand; }
    public void setBrand(String v){ this.brand=v; }
    public String getDescription(){ return description; }
    public void setDescription(String v){ this.description=v; }
    public BigDecimal getPrice(){ return price; }
    public void setPrice(BigDecimal v){ this.price=v; }
    public BigDecimal getMrp(){ return mrp; }
    public void setMrp(BigDecimal v){ this.mrp=v; }
    public int getStock(){ return stock; }
    public void setStock(int v){ this.stock=v; }
    public String getImageUrl(){ return imageUrl; }
    public void setImageUrl(String v){ this.imageUrl=v; }
    public boolean isActive(){ return active; }
    public void setActive(boolean v){ this.active=v; }
    public double getAvgRating(){ return avgRating; }
    public void setAvgRating(double v){ this.avgRating=v; }
    public int getReviewCount(){ return reviewCount; }
    public void setReviewCount(int v){ this.reviewCount=v; }
    public int getSellerId(){ return sellerId; }
    public void setSellerId(int v){ this.sellerId=v; }
    public String getSellerName(){ return sellerName; }
    public void setSellerName(String v){ this.sellerName=v; }
    public String getSellerStatus(){ return sellerStatus; }
    public void setSellerStatus(String v){ this.sellerStatus=v; }
    public BigDecimal getGstPercent(){ return gstPercent; }
    public void setGstPercent(BigDecimal v){ this.gstPercent=v; }
    public String getHsnCode(){ return hsnCode; }
    public void setHsnCode(String v){ this.hsnCode=v; }
    /** Store-owned products (no seller) are always allowed; seller products only while the seller is approved. */
    public boolean isSellerLive(){ return sellerId == 0 || "APPROVED".equals(sellerStatus); }
    /** Visible and buyable in the store. */
    public boolean isLive(){ return active && isSellerLive(); }
    public String getSoldBy(){ return sellerName == null || sellerName.isEmpty() ? "ecart" : sellerName; }
    public int getDiscountPercent(){
        if(mrp==null||price==null||mrp.signum()<=0||price.compareTo(mrp)>=0) return 0;
        return mrp.subtract(price).multiply(BigDecimal.valueOf(100)).divide(mrp,0,RoundingMode.DOWN).intValue();
    }
    public int getMaxQty(){ return Math.max(0, Math.min(stock, 10)); }
    public int getRoundedRating(){ return (int)Math.round(avgRating); }
}
