package com.agongym.store.database.models;

public class CartModel {

    private String productId;
    private String VariantId;
    private String quantity;
    private String variantPrice;

    public CartModel(String productId, String variantId, String quantity, String variantPrice) {
        this.productId = productId;
        this.VariantId = variantId;
        this.quantity = quantity;
        this.variantPrice = variantPrice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getVariantId() {
        return VariantId;
    }

    public void setVariantId(String variantId) {
        VariantId = variantId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getVariantPrice() {
        return variantPrice;
    }

    public void setVariantPrice(String variantPrice) {
        this.variantPrice = variantPrice;
    }
}
