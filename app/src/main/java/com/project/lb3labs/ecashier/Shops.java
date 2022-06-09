package com.project.lb3labs.ecashier;

public class Shops {
    private String title, unit;
    private Double qty;
    private int priceOne;
    private int totalPriceOne;
    private int productId;

    // Constructor that is used to create an instance of the Movie object
    Shops(String title, String unit, double qty, int priceOne,int totalPriceOne, int prod_id) {
        this.title = title;
        this.unit = unit;
        this.qty = qty;
        this.priceOne = priceOne;
        this.totalPriceOne = totalPriceOne;
        this.productId = prod_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public int getPriceOne() {
        return priceOne;
    }

    public void setPriceOne(int priceOne) {
        this.priceOne = priceOne;
    }

    public int getTotalPriceOne() {
        return totalPriceOne;
    }

    public void setTotalPriceOne(int priceOne) {
        this.totalPriceOne = priceOne;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int priceOne) {
        this.productId = productId;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
