package com.montelzek.paymentoptimizer.model;

import java.math.BigDecimal;
import java.util.List;

public class Order {

    private String id;

    private BigDecimal value;

    private List<String> promotions;

    public Order() {
    }

    public Order(String id, BigDecimal value, List<String> promotions) {
        this.id = id;
        this.value = value;
        this.promotions = promotions;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getValue() {
        return value;
    }

    public List<String> getPromotions() {
        return promotions;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public void setPromotions(List<String> promotions) {
        this.promotions = promotions;
    }

}
