package com.montelzek.paymentoptimizer.model;

import java.math.BigDecimal;

public class PaymentMethod {

    private String id;

    private BigDecimal discount;

    private BigDecimal limit;

    public PaymentMethod() {
    }

    public PaymentMethod(String id, BigDecimal discount, BigDecimal limit) {
        this.id = id;
        this.discount = discount;
        this.limit = limit;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public void setLimit(BigDecimal limit) {
        this.limit = limit;
    }

}
