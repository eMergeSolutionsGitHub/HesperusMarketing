package com.HesperusMarketing.Entity;

/**
 * Created by Amila on 11/15/15.
 */
public class TempInvoiceStock {

    private String productCode;
    private String productId;
    private String batchCode;
    private String shelfQuantity;
    private String requestQuantity;
    private String freeQuantity;
    private String normalQuantity;
    private String row_ID;
    private int Stock;
    private double percentage;
    private String expiryDate;
    private String timestamp;
    private String productDes;
    private String price;
    private String isFreeAllowed;
    private String isDiscountAllowed;
    private String retailsPrice;
    private  String category;
    private  String principle;
    private  String proImage;

    public String getProImage() {
        return proImage;
    }

    public void setProImage(String proImage) {
        this.proImage = proImage;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrinciple() {
        return principle;
    }

    public void setPrinciple(String principle) {
        this.principle = principle;
    }

    public String getRetailsPrice() {
        return retailsPrice;
    }

    public void setRetailsPrice(String retailsPrice) {
        this.retailsPrice = retailsPrice;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getShelfQuantity() {
        return shelfQuantity;
    }

    public void setShelfQuantity(String shelfQuantity) {
        this.shelfQuantity = shelfQuantity;
    }

    public String getRequestQuantity() {
        return requestQuantity;
    }

    public void setRequestQuantity(String requestQuantity) {
        this.requestQuantity = requestQuantity;
    }

    public String getFreeQuantity() {
        return freeQuantity;
    }

    public void setFreeQuantity(String freeQuantity) {
        this.freeQuantity = freeQuantity;
    }

    public String getNormalQuantity() {
        return normalQuantity;
    }

    public void setNormalQuantity(String normalQuantity) {
        this.normalQuantity = normalQuantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getRow_ID() {
        return row_ID;
    }

    public void setRow_ID(String row_ID) {
        this.row_ID = row_ID;
    }

    public int getStock() {
        return Stock;
    }

    public void setStock(int stock) {
        Stock = stock;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getProductDes() {
        return productDes;
    }

    public void setProductDes(String productDes) {
        this.productDes = productDes;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


    public String getIsFreeAllowed() {
        return isFreeAllowed;
    }

    public void setIsFreeAllowed(String isFreeAllowed) {
        this.isFreeAllowed = isFreeAllowed;
    }

    public String getIsDiscountAllowed() {
        return isDiscountAllowed;
    }

    public void setIsDiscountAllowed(String isDiscountAllowed) {
        this.isDiscountAllowed = isDiscountAllowed;
    }
}