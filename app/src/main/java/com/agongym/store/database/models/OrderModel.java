package com.agongym.store.database.models;

public class OrderModel {
    private String CustomerAccessToken;
    private String name;
    private String price;
    private String processedAt;

    public OrderModel(String customerAccessToken, String name, String price, String processedAt) {
        this.CustomerAccessToken = customerAccessToken;
        this.name = name;
        this.price = price;
        this.processedAt = processedAt;
    }

    public String getCustomerAccessToken() {
        return CustomerAccessToken;
    }

    public void setCustomerAccessToken(String customerAccessToken) {
        CustomerAccessToken = customerAccessToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(String processedAt) {
        this.processedAt = processedAt;
    }
}
