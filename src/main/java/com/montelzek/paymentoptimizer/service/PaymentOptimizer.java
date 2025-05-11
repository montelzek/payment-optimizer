package com.montelzek.paymentoptimizer.service;

import com.montelzek.paymentoptimizer.model.Order;
import com.montelzek.paymentoptimizer.model.PaymentMethod;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class PaymentOptimizer {

    private static final RoundingMode roundingMode = RoundingMode.HALF_UP;

    public static Map<String, BigDecimal> processOrders(List<Order> orders, List<PaymentMethod> methods) {

        Map<String, BigDecimal> limits = new HashMap<>();
        Map<String, BigDecimal> discounts = new HashMap<>();
        for (PaymentMethod paymentMethod : methods) {
            limits.put(paymentMethod.getId(), paymentMethod.getLimit().setScale(2, roundingMode));
            discounts.put(paymentMethod.getId(), paymentMethod.getDiscount());
        }

        Map<String, BigDecimal> result = new HashMap<>();

        for (Order order : orders) {
            BigDecimal value = order.getValue().setScale(2, roundingMode);
            boolean paid = false;

            // FULL PAYMENT WITH POINTS WITH DISCOUNT
            BigDecimal pointsLimit = limits.getOrDefault("PUNKTY", BigDecimal.ZERO);
            BigDecimal pointsDiscount = discounts.getOrDefault("PUNKTY", BigDecimal.ZERO);
            if (pointsLimit.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal finalPrice = value.subtract(value.multiply(pointsDiscount)
                        .divide(BigDecimal.valueOf(100), 2, roundingMode))
                        .setScale(2, roundingMode);
                if (pointsLimit.compareTo(finalPrice) >= 0) {
                    result.put("PUNKTY", result.getOrDefault("PUNKTY", BigDecimal.ZERO).add(finalPrice));
                    limits.put("PUNKTY", pointsLimit.subtract(finalPrice));
                    paid = true;
                }
            }
            if (paid) continue;

            // FULL PAYMENT WITH CARD WITH DISCOUNT
            List<String> promos = order.getPromotions() != null ? order.getPromotions() : List.of();
            String bestPromo = null;
            BigDecimal bestDiscount = BigDecimal.ZERO;
            for (String promo : promos) {
                BigDecimal discount = discounts.getOrDefault(promo, BigDecimal.ZERO);
                if (discount.compareTo(bestDiscount) > 0 && limits.getOrDefault(promo, BigDecimal.ZERO).compareTo(value) >= 0) {
                    bestPromo = promo;
                    bestDiscount = discount;
                }
            }
            if (bestPromo != null) {
                BigDecimal finalPrice = value.subtract(value.multiply(bestDiscount).divide(BigDecimal.valueOf(100), 2, roundingMode));
                finalPrice = finalPrice.setScale(2, roundingMode);
                limits.put(bestPromo, limits.get(bestPromo).subtract(finalPrice));
                result.put(bestPromo, result.getOrDefault(bestPromo, BigDecimal.ZERO).add(finalPrice));
                paid = true;
            }
            if (paid) continue;

            // PARTIAL POINTS WITH 10% DISCOUNT
            BigDecimal minPoints = value.multiply(new BigDecimal("0.10")).setScale(2, roundingMode);
            if (limits.getOrDefault("PUNKTY", BigDecimal.ZERO).compareTo(minPoints) >= 0) {
                BigDecimal discountTotal = value.multiply(new BigDecimal("10")).divide(BigDecimal.valueOf(100), 2, roundingMode);
                BigDecimal afterDiscount = value.subtract(discountTotal).setScale(2, roundingMode);
                BigDecimal usePoints = limits.get("PUNKTY").min(afterDiscount);
                usePoints = usePoints.max(minPoints);
                BigDecimal remaining = afterDiscount.subtract(usePoints);
                for (String method : limits.keySet()) {
                    if (!method.equals("PUNKTY") && limits.get(method).compareTo(remaining) >= 0) {
                        result.put("PUNKTY", result.getOrDefault("PUNKTY", BigDecimal.ZERO).add(usePoints));
                        result.put(method, result.getOrDefault(method, BigDecimal.ZERO).add(remaining));
                        limits.put("PUNKTY", limits.get("PUNKTY").subtract(usePoints));
                        limits.put(method, limits.get(method).subtract(remaining));
                        paid = true;
                        break;
                    }
                }
                if (paid) continue;
            }

            // PARTIAL PAYMENT BY POINTS AND THE REST BY CARD | NO DISCOUNT
            if (limits.containsKey("PUNKTY") && limits.get("PUNKTY").compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal usePoints = limits.get("PUNKTY").min(value);
                BigDecimal remaining = value.subtract(usePoints);
                for (String method : limits.keySet()) {
                    if (!method.equals("PUNKTY") && limits.get(method).compareTo(remaining) >= 0) {
                        result.put("PUNKTY", result.getOrDefault("PUNKTY", BigDecimal.ZERO).add(usePoints));
                        result.put(method, result.getOrDefault(method, BigDecimal.ZERO).add(remaining));
                        limits.put("PUNKTY", limits.get("PUNKTY").subtract(usePoints));
                        limits.put(method, limits.get(method).subtract(remaining));
                        paid = true;
                        break;
                    }
                }
            }
            if (paid) continue;

            // FULL CARD PAYMENT WITH NO DISCOUNT
            for (String method : limits.keySet()) {
                if (!method.equals("PUNKTY") && limits.get(method).compareTo(value) >= 0) {
                    result.put(method, result.getOrDefault(method, BigDecimal.ZERO).add(value));
                    limits.put(method, limits.get(method).subtract(value));
                    paid = true;
                    break;
                }
            }

            if (!paid) {
                System.out.println("Nie można przetworzyć zamówienia " + order.getId());
            }
        }

        return result;
    }


}
