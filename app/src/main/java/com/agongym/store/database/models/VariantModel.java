package com.agongym.store.database.models;

public class VariantModel {
    private String id;
    private String productId;
    private String availableForSale;
    private String sku;
    private String title;
    private String price;
    private String compareAtPrice;

    public VariantModel(String id, String productId, String availableForSale, String sku, String title, String price, String compareAtPrice) {
        this.id = id;
        this.productId = productId;
        this.availableForSale = availableForSale;
        this.sku = sku;
        this.title = title;
        this.price = price;
        this.compareAtPrice = compareAtPrice;

    }

    public String getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvailableForSale() {
        return availableForSale;
    }

    public void setAvailableForSale(String availableForSale) {
        this.availableForSale = availableForSale;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCompareAtPrice() {
        return compareAtPrice;
    }

    public void setCompareAtPrice(String compareAtPrice) {
        this.compareAtPrice = compareAtPrice;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
