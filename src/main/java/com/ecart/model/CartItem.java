package com.ecart.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;

public class CartItem {
    private Product product;
    private int qty;

    public Product getProduct(){ return product; }
    public void setProduct(Product v){ this.product=v; }
    public int getQty(){ return qty; }
    public void setQty(int v){ this.qty=v; }
    public BigDecimal getLineTotal(){ return product.getPrice().multiply(BigDecimal.valueOf(qty)); }
}
