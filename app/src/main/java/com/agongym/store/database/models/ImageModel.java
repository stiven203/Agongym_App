package com.agongym.store.database.models;

public class ImageModel {

    private String id;
    private String productId;
    private String originalSrc;

    public ImageModel(String id, String productId, String originalSrc) {
        this.id = id;
        this.productId = productId;
        this.originalSrc = originalSrc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginalSrc() {
        return originalSrc;
    }

    public void setOriginalSrc(String originalSrc) {
        this.originalSrc = originalSrc;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
