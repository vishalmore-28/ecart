package com.Bean;

import java.io.Serializable;

public class ProductBean implements Serializable {
   String pcode;
   String pname;
   String pcompany;
   String price;
   String pqty;

   public ProductBean() {
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

   public String getPqty() {
      return this.pqty;
   }

   public void setPqty(String pqty) {
      this.pqty = pqty;
   }
}
