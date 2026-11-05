package com.ecart.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;

public class Review {
    private int id;
    private int productId;
    private int userId;
    private String userName;
    private int rating;
    private String title;
    private String body;
    private boolean verified;
    private Timestamp createdAt;

    public int getId(){ return id; }
    public void setId(int v){ this.id=v; }
    public int getProductId(){ return productId; }
    public void setProductId(int v){ this.productId=v; }
    public int getUserId(){ return userId; }
    public void setUserId(int v){ this.userId=v; }
    public String getUserName(){ return userName; }
    public void setUserName(String v){ this.userName=v; }
    public int getRating(){ return rating; }
    public void setRating(int v){ this.rating=v; }
    public String getTitle(){ return title; }
    public void setTitle(String v){ this.title=v; }
    public String getBody(){ return body; }
    public void setBody(String v){ this.body=v; }
    public boolean isVerified(){ return verified; }
    public void setVerified(boolean v){ this.verified=v; }
    public Timestamp getCreatedAt(){ return createdAt; }
    public void setCreatedAt(Timestamp v){ this.createdAt=v; }
}
