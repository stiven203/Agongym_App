package com.agongym.store.database.models;
import java.io.Serializable;

public class ProductModel implements Serializable {

    private String id;
    private String title;
    private String availableForSale; 
    private String description;
    private String productType;
    private String mainImage;
    private String maxVariantPrice;
    private String collection;

    public ProductModel(String id, String title, String availableForSale, String description, String productType, String mainImage, String maxVariantPrice, String collection) {
        this.id = id;
        this.title = title;
        this.availableForSale=availableForSale;
        this.description=description;
        this.productType=productType;
        this.mainImage=mainImage;
        this.maxVariantPrice=maxVariantPrice;
        this.collection=collection;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public String getMaxVariantPrice() {
        return maxVariantPrice;
    }

    public void setMaxVariantPrice(String maxVariantPrice) {
        this.maxVariantPrice = maxVariantPrice;
    }

    public String getAvailableForSale() {
        return availableForSale;
    }

    public void setAvailableForSale(String availableForSale) {
        this.availableForSale = availableForSale;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }
}
