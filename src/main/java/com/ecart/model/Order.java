package com.ecart.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;

public class Order {
    private int id;
    private String orderNo;
    private int userId;
    private String userName;
    private String status;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal shipping;
    private BigDecimal total;
    private String couponCode;
    private String shipName;
    private String shipPhone;
    private String shipAddress;
    private String paymentMethod;
    private String paymentStatus;
    private String txnRef;
    private String cardLast4;
    private Timestamp createdAt;
    private List<OrderItem> items;
    private List<OrderEvent> events;
    private String shipState;
    private BigDecimal mineTaxable = BigDecimal.ZERO;
    private BigDecimal mineTax = BigDecimal.ZERO;
    private boolean sameState;

    public int getId(){ return id; }
    public void setId(int v){ this.id=v; }
    public String getOrderNo(){ return orderNo; }
    public void setOrderNo(String v){ this.orderNo=v; }
    public int getUserId(){ return userId; }
    public void setUserId(int v){ this.userId=v; }
    public String getUserName(){ return userName; }
    public void setUserName(String v){ this.userName=v; }
    public String getStatus(){ return status; }
    public void setStatus(String v){ this.status=v; }
    public BigDecimal getSubtotal(){ return subtotal; }
    public void setSubtotal(BigDecimal v){ this.subtotal=v; }
    public BigDecimal getDiscount(){ return discount; }
    public void setDiscount(BigDecimal v){ this.discount=v; }
    public BigDecimal getTax(){ return tax; }
    public void setTax(BigDecimal v){ this.tax=v; }
    public BigDecimal getShipping(){ return shipping; }
    public void setShipping(BigDecimal v){ this.shipping=v; }
    public BigDecimal getTotal(){ return total; }
    public void setTotal(BigDecimal v){ this.total=v; }
    public String getCouponCode(){ return couponCode; }
    public void setCouponCode(String v){ this.couponCode=v; }
    public String getShipName(){ return shipName; }
    public void setShipName(String v){ this.shipName=v; }
    public String getShipPhone(){ return shipPhone; }
    public void setShipPhone(String v){ this.shipPhone=v; }
    public String getShipAddress(){ return shipAddress; }
    public void setShipAddress(String v){ this.shipAddress=v; }
    public String getPaymentMethod(){ return paymentMethod; }
    public void setPaymentMethod(String v){ this.paymentMethod=v; }
    public String getPaymentStatus(){ return paymentStatus; }
    public void setPaymentStatus(String v){ this.paymentStatus=v; }
    public String getTxnRef(){ return txnRef; }
    public void setTxnRef(String v){ this.txnRef=v; }
    public String getCardLast4(){ return cardLast4; }
    public void setCardLast4(String v){ this.cardLast4=v; }
    public Timestamp getCreatedAt(){ return createdAt; }
    public void setCreatedAt(Timestamp v){ this.createdAt=v; }
    public List<OrderItem> getItems(){ return items; }
    public void setItems(List<OrderItem> v){ this.items=v; }
    public List<OrderEvent> getEvents(){ return events; }
    public void setEvents(List<OrderEvent> v){ this.events=v; }
    public String getShipState(){ return shipState; }
    public void setShipState(String v){ this.shipState=v; }
    /** Seller view: taxable value / GST of this seller's items only. */
    public BigDecimal getMineTaxable(){ return mineTaxable; }
    public void setMineTaxable(BigDecimal v){ this.mineTaxable=v; }
    public BigDecimal getMineTax(){ return mineTax; }
    public void setMineTax(BigDecimal v){ this.mineTax=v; }
    public BigDecimal getMineTotal(){ return mineTaxable.add(mineTax); }
    /** True when seller state equals the delivery state: CGST + SGST instead of IGST. */
    public boolean isSameState(){ return sameState; }
    public void setSameState(boolean v){ this.sameState=v; }
    public boolean isCancellable(){ return "PLACED".equals(status) || "CONFIRMED".equals(status); }
    public int getItemCount(){ int n=0; if(items!=null) for(OrderItem i:items) n+=i.getQty(); return n; }
}
