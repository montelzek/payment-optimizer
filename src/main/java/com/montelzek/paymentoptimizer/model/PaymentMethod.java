package com.montelzek.paymentoptimizer.model;

import java.math.BigDecimal;

public class PaymentMethod {

    private String id;

    private int discount;

    private BigDecimal limit;

    public String getId() {
        return id;
    }

    public int getDiscount() {
        return discount;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public void setLimit(BigDecimal limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "PaymentMethod{" +
                "id='" + id + '\'' +
                ", discount=" + discount +
                ", limit=" + limit +
                '}';
    }
}
