package com.ecart.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;

public class Category {
    private int id;
    private String name;
    private String icon;

    public int getId(){ return id; }
    public void setId(int v){ this.id=v; }
    public String getName(){ return name; }
    public void setName(String v){ this.name=v; }
    public String getIcon(){ return icon; }
    public void setIcon(String v){ this.icon=v; }
}
