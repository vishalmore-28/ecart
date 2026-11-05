package com.ecart.model;

import java.sql.Timestamp;

public class Seller {
    private int userId;
    private String ownerName, email, phone;
    private String shopName, businessType, pan, gstin;
    private boolean gstRegistered;
    private String addressLine1, addressLine2, city, state, pincode;
    private String bankHolder, bankIfsc, bankLast4;
    private String status, reviewNote;
    private Timestamp createdAt, reviewedAt;

    public int getUserId(){ return userId; }
    public void setUserId(int v){ this.userId=v; }
    public String getOwnerName(){ return ownerName; }
    public void setOwnerName(String v){ this.ownerName=v; }
    public String getEmail(){ return email; }
    public void setEmail(String v){ this.email=v; }
    public String getPhone(){ return phone; }
    public void setPhone(String v){ this.phone=v; }
    public String getShopName(){ return shopName; }
    public void setShopName(String v){ this.shopName=v; }
    public String getBusinessType(){ return businessType; }
    public void setBusinessType(String v){ this.businessType=v; }
    public String getPan(){ return pan; }
    public void setPan(String v){ this.pan=v; }
    public String getGstin(){ return gstin; }
    public void setGstin(String v){ this.gstin=v; }
    public boolean isGstRegistered(){ return gstRegistered; }
    public void setGstRegistered(boolean v){ this.gstRegistered=v; }
    public String getAddressLine1(){ return addressLine1; }
    public void setAddressLine1(String v){ this.addressLine1=v; }
    public String getAddressLine2(){ return addressLine2; }
    public void setAddressLine2(String v){ this.addressLine2=v; }
    public String getCity(){ return city; }
    public void setCity(String v){ this.city=v; }
    public String getState(){ return state; }
    public void setState(String v){ this.state=v; }
    public String getPincode(){ return pincode; }
    public void setPincode(String v){ this.pincode=v; }
    public String getBankHolder(){ return bankHolder; }
    public void setBankHolder(String v){ this.bankHolder=v; }
    public String getBankIfsc(){ return bankIfsc; }
    public void setBankIfsc(String v){ this.bankIfsc=v; }
    public String getBankLast4(){ return bankLast4; }
    public void setBankLast4(String v){ this.bankLast4=v; }
    public String getStatus(){ return status; }
    public void setStatus(String v){ this.status=v; }
    public String getReviewNote(){ return reviewNote; }
    public void setReviewNote(String v){ this.reviewNote=v; }
    public Timestamp getCreatedAt(){ return createdAt; }
    public void setCreatedAt(Timestamp v){ this.createdAt=v; }
    public Timestamp getReviewedAt(){ return reviewedAt; }
    public void setReviewedAt(Timestamp v){ this.reviewedAt=v; }

    public boolean isApproved(){ return "APPROVED".equals(status); }
    public boolean isPending(){ return "PENDING".equals(status); }
    /** CSS class reused from the order-status chips: green / amber / red. */
    public String getChip(){ return isApproved() ? "DELIVERED" : isPending() ? "SHIPPED" : "CANCELLED"; }
    public String getFullAddress(){
        return addressLine1 + (addressLine2 == null || addressLine2.isEmpty() ? "" : ", " + addressLine2) + ", " + city + ", " + state + " - " + pincode;
    }
}
