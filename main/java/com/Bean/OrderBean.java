package com.Bean;

import java.io.Serializable;

public class OrderBean implements Serializable {
   private static final long serialVersionUID = 1L;

   String customer;
   String pcode;
   String pname;
   String pcompany;
   String price;
   String qty;

   public OrderBean() {
   }

   public String getCustomer() {
      return this.customer;
   }

   public void setCustomer(String customer) {
      this.customer = customer;
   }

   public String getPcode() {
      return this.pcode;
   }

   public void setPcode(String pcode) {
      this.pcode = pcode;
   }

   public String getPname() {
      return this.pname;
   }

   public void setPname(String pname) {
      this.pname = pname;
   }

   public String getPcompany() {
      return this.pcompany;
   }

   public void setPcompany(String pcompany) {
      this.pcompany = pcompany;
   }

   public String getPrice() {
      return this.price;
   }

   public void setPrice(String price) {
      this.price = price;
   }

   public String getQty() {
      return this.qty;
   }

   public void setQty(String qty) {
      this.qty = qty;
   }
}
