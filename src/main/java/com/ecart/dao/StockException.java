package com.ecart.dao;

public class StockException extends Exception {
    public StockException(String productName) { super("Not enough stock for " + productName); }
}
