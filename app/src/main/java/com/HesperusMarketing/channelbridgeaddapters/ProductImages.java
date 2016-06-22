package com.HesperusMarketing.channelbridgeaddapters;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Himanshu on 3/28/2016.
 */
public class ProductImages {

    String itemCode;
    String batch;
    String itemImage;
    String shelf;
    String request;
    String order;
    String free;
    String stock;


    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public String getShelf() {
        return shelf;
    }

    public void setShelf(String shelf) {
        this.shelf = shelf;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getFree() {
        return free;
    }

    public void setFree(String free) {
        this.free = free;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public ProductImages(String itemCode,String batch, String itemImage, String shelf, String request, String order, String free, String stock) {
        this.itemCode = itemCode;
        this.batch = batch;
        this.itemImage = itemImage;
        this.shelf = shelf;
        this.request = request;
        this.order = order;
        this.free = free;
        this.stock = stock;
    }


}
