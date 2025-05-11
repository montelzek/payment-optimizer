package com.montelzek.paymentoptimizer.service;

import com.montelzek.paymentoptimizer.model.Order;
import com.montelzek.paymentoptimizer.model.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PaymentOptimizerTest {

    private List<Order> orders;
    private List<PaymentMethod> methods;

    @BeforeEach
    void setUp() {
        orders = new ArrayList<>();
        methods = new ArrayList<>();
    }

    private BigDecimal val(String value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
    }

    @Test
    void processOrders_fullPaymentWithPoints_discountApplied() {

        // Arrange
        methods.add(new PaymentMethod("PUNKTY", val("15"), val("200.00")));
        orders.add(new Order("ORDER1", val("100.00"), Collections.emptyList()));

        // Act
        Map<String, BigDecimal> result = PaymentOptimizer.processOrders(orders, methods);

        // Assert
        assertNotNull(result.get("PUNKTY"));
        assertEquals(val("85.00"), result.get("PUNKTY"));
    }

    @Test
    void processOrders_fullPaymentWithCardDiscount() {

        // Arrange
        methods.add(new PaymentMethod("CardA", val("10"), val("200.00")));
        orders.add(new Order("ORDER1", val("100.00"), Collections.singletonList("CardA")));

        // Act
        Map<String, BigDecimal> result = PaymentOptimizer.processOrders(orders, methods);

        // Assert
        assertNotNull(result.get("CardA"));
        assertEquals(val("90.00"), result.get("CardA"));
        assertEquals(1, result.size());
    }

    @Test
    void processOrders_partialPointsWith10PercentDiscount_thenCard() {

        // Arrange
        methods.add(new PaymentMethod("PUNKTY", val("0"), val("50.00")));
        methods.add(new PaymentMethod("CardB", val("0"), val("100.00")));
        orders.add(new Order("ORDER1", val("100.00"), Collections.emptyList()));

        // Act
        Map<String, BigDecimal> result = PaymentOptimizer.processOrders(orders, methods);

        // Assert
        assertEquals(val("50.00"), result.get("PUNKTY"));
        assertEquals(val("40.00"), result.get("CardB"));
    }


    @Test
    void processOrders_noDiscount_fullPaymentWithFirstAvailableCard() {

        // Arrange
        methods.add(new PaymentMethod("CardX", val("0"), val("30.00")));
        methods.add(new PaymentMethod("CardY", val("0"), val("100.00")));
        orders.add(new Order("ORDER1", val("50.00"), Collections.emptyList()));

        // Act
        Map<String, BigDecimal> result = PaymentOptimizer.processOrders(orders, methods);

        // Assert
        assertNull(result.get("CardX"));
        assertNotNull(result.get("CardY"));
        assertEquals(val("50.00"), result.get("CardY"));
    }

    @Test
    void processOrders_orderCannotBePaid_ifNoSufficientFunds() {

        // Arrange
        methods.add(new PaymentMethod("CardZ", val("0"), val("10.00")));
        orders.add(new Order("ORDER1", val("100.00"), Collections.emptyList()));

        // Act
        Map<String, BigDecimal> result = PaymentOptimizer.processOrders(orders, methods);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void processOrders_selectsBestPromotionalCard() {

        // Arrange
        methods.add(new PaymentMethod("PromoCardLow", val("5"), val("200")));
        methods.add(new PaymentMethod("PromoCardHigh", val("10"), val("200")));
        methods.add(new PaymentMethod("PromoCardNoLimit", val("15"), val("50")));
        orders.add(new Order("ORDER1", val("100"), Arrays.asList("PromoCardLow", "PromoCardHigh", "PromoCardNoLimit")));

        // Act
        Map<String, BigDecimal> result = PaymentOptimizer.processOrders(orders, methods);

        // Assert
        assertEquals(val("90.00"), result.get("PromoCardHigh"));
        assertNull(result.get("PromoCardLow"));
        assertNull(result.get("PromoCardNoLimit"));
    }

    @Test
    void processOrders_partialPointsNoDiscount_thenCard() {

        // Arrange
        methods.add(new PaymentMethod("PUNKTY", val("0"), val("5.00")));
        methods.add(new PaymentMethod("CardC", val("0"), val("100.00")));
        orders.add(new Order("ORDER1", val("100.00"), Collections.emptyList()));

        // Act
        Map<String, BigDecimal> result = PaymentOptimizer.processOrders(orders, methods);

        // Assert
        assertEquals(val("5.00"), result.get("PUNKTY"));
        assertEquals(val("95.00"), result.get("CardC"));
    }
}