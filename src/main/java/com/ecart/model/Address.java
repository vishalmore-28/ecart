package com.ecart.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;

public class Address {
    private int id;
    private int userId;
    private String fullName;
    private String phone;
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String pincode;
    private boolean defaultAddress;

    public int getId(){ return id; }
    public void setId(int v){ this.id=v; }
    public int getUserId(){ return userId; }
    public void setUserId(int v){ this.userId=v; }
    public String getFullName(){ return fullName; }
    public void setFullName(String v){ this.fullName=v; }
    public String getPhone(){ return phone; }
    public void setPhone(String v){ this.phone=v; }
    public String getLine1(){ return line1; }
    public void setLine1(String v){ this.line1=v; }
    public String getLine2(){ return line2; }
    public void setLine2(String v){ this.line2=v; }
    public String getCity(){ return city; }
    public void setCity(String v){ this.city=v; }
    public String getState(){ return state; }
    public void setState(String v){ this.state=v; }
    public String getPincode(){ return pincode; }
    public void setPincode(String v){ this.pincode=v; }
    public boolean isDefaultAddress(){ return defaultAddress; }
    public void setDefaultAddress(boolean v){ this.defaultAddress=v; }
    public String getFull(){
        StringBuilder b=new StringBuilder(fullName).append(", ").append(line1);
        if(line2!=null && !line2.isEmpty()) b.append(", ").append(line2);
        return b.append(", ").append(city).append(", ").append(state).append(" - ").append(pincode).append(" | Ph: ").append(phone).toString();
    }
}
