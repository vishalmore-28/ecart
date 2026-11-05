package com.ecart.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;

public class User {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private boolean active;
    private String passwordHash;
    private int failedAttempts;
    private Timestamp lockedUntil;
    private Timestamp createdAt;

    public int getId(){ return id; }
    public void setId(int v){ this.id=v; }
    public String getName(){ return name; }
    public void setName(String v){ this.name=v; }
    public String getEmail(){ return email; }
    public void setEmail(String v){ this.email=v; }
    public String getPhone(){ return phone; }
    public void setPhone(String v){ this.phone=v; }
    public String getRole(){ return role; }
    public void setRole(String v){ this.role=v; }
    public boolean isActive(){ return active; }
    public void setActive(boolean v){ this.active=v; }
    public String getPasswordHash(){ return passwordHash; }
    public void setPasswordHash(String v){ this.passwordHash=v; }
    public int getFailedAttempts(){ return failedAttempts; }
    public void setFailedAttempts(int v){ this.failedAttempts=v; }
    public Timestamp getLockedUntil(){ return lockedUntil; }
    public void setLockedUntil(Timestamp v){ this.lockedUntil=v; }
    public Timestamp getCreatedAt(){ return createdAt; }
    public void setCreatedAt(Timestamp v){ this.createdAt=v; }
    public boolean isAdmin(){ return "ADMIN".equals(role); }
    public boolean isSeller(){ return "SELLER".equals(role); }
}
