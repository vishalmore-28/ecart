package com.ecart.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;

public class OrderEvent {
    private String status;
    private String note;
    private Timestamp createdAt;

    public String getStatus(){ return status; }
    public void setStatus(String v){ this.status=v; }
    public String getNote(){ return note; }
    public void setNote(String v){ this.note=v; }
    public Timestamp getCreatedAt(){ return createdAt; }
    public void setCreatedAt(Timestamp v){ this.createdAt=v; }
}
