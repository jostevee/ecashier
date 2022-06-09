package com.project.lb3labs.ecashier;

public class setProduct {
    String product;
    int priceOne;
    int productId;

    // Constructor that is used to create an instance of the Movie object
    public setProduct(String nama, int harga, int prod_id) {
        this.product = nama;
        this.priceOne = harga;
        this.productId = prod_id;
    }

    public String getProduct() {
        return product;
    }

    int getPriceOne() {
        return priceOne;
    }

    int getProductId() {
        return productId;
    }
}
